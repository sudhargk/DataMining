function[fcorr,scoreCorr] = correlationBWClusters()
    load('dataset.mat');
    binIndex = 10;
    remove = 2000;
    [~,idx] = sort(M(:,binIndex),'ascend');
    M=M(idx(remove+1:end),:);
%     binIndex=9;
    numBins = 2;
    %[N,X]= hist(M(:,binIndex),numBins);
    %base = X(1);
%     p=fliplr(ones(1,numBins)./(1:numBins));
%     X=quantile(M(:,binIndex),p);
    X = [1 max(M(:,binIndex))];
    base=0;
    
    numSamples = size(M,1);
    trainSamples = floor(0.70*numSamples);
    test = ones(numSamples-trainSamples,8);
    
    ridx = randperm(numSamples);
    test(:,2:end)= M(ridx((trainSamples+1):numSamples),2:8);
    testT = M(ridx(trainSamples+1:numSamples),9:11);
    
    M=M(ridx(1:trainSamples),:);
     
    cols = 21;
    fcorr = zeros(numBins,cols);
    scoreCorr = zeros(numBins,1);
    scoreCorrN = zeros(numBins,1);
    error = cell(numBins,1);
    W = cell(numBins,1);
    LB = 0;
    
    ftake = 1:8;
    
    for idx = 1:numBins
        UB = X(idx)+base;
        logicals = M(:,binIndex)<UB & M(:,binIndex)>=LB;
        if(~sum(logicals)==0)
            A = M(logicals,:);
            trainSamples = sum(logicals);
            train = ones(trainSamples,8);
                         
            train(:,2:end)=A(:,2:8);
            trainT = A(:,9:11);
            
            
            W{idx}  = regress(trainT(:,1),train(:,ftake));
            tTest = test(:,ftake);
            scoreCorr(idx) = corr(tTest*W{idx},testT(:,1));
            error{idx}=testT(:,1)-tTest*W{idx};
            
            %Computing correlationg between features and outputs
            tcorr= corr(A(:,2:8),A(:,9:11));
            fcorr(idx,:) =  tcorr(:); 
        end
        LB  = UB; 
    end
    LB = 0;
    for idx = 1:numBins
        UB = X(idx)+base;
        logicals = testT(:,2)<UB & testT(:,2)>=LB;
         if(~sum(logicals)==0)
             tTest = test(logicals,ftake);
             scoreCorrN(idx) = corr(tTest*W{idx},testT(logicals,1));
         end
        LB  = UB; 
    end
    display(X);
    display(fcorr);
    display(scoreCorr);
    display(scoreCorrN);
    display(error);
end