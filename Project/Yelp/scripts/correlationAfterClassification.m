function[fcorr,scoreCorr] = correlationAfterClassification()
    load('dataset19Apr.mat');
    binIndex = 11;
    remove = 2000;
    [~,idx] = sort(M(:,binIndex),'ascend');
    M=M(idx(remove+1:end),:);
    numBins = 2;
%     [N,X]= hist(M(:,binIndex),numBins);
%     display(N);
%     base = X(1);
%     p=fliplr(ones(1,numBins)./(1:numBins));
%     X=quantile(M(:,binIndex),p);
     X = [1 max(M(:,binIndex))];
     base=0;
    
    numSamples = size(M,1);
    trainSamples = floor(0.70*numSamples);
    test = ones(numSamples-trainSamples,9);
    
    ridx = randperm(numSamples);
    test(:,2:end)= M(ridx((trainSamples+1):numSamples),2:9);
    testT = M(ridx(trainSamples+1:numSamples),10:12);
    
    M=M(ridx(1:trainSamples),:);
    
     
    cols = 24;
    fcorr = zeros(numBins,cols);
    scoreCorr = zeros(numBins,1);
    scoreCorrN = zeros(numBins,1);
    error = cell(numBins,1);
    W = cell(numBins,1);
%     models = cell(numBins,1);
    
    LB = -1;
    display(sum(M(:,binIndex)));
    ftake = 1:9;
    
    models = svmtrain(double(M(:,binIndex)>1+1),M(:,2:9),'-s 1 -t 2 -n 0.2');
    for idx = 1:numBins
        UB = X(idx)+base;
        logicals = M(:,binIndex)<=UB & M(:,binIndex)>LB;
        if(~sum(logicals)==0)
            A = M(logicals,:);
            trainSamples = sum(logicals);
            train = ones(trainSamples,9);
                         
            train(:,2:end)=A(:,2:9);
            trainT = A(:,10:12);
          
            W{idx}  = regress(trainT(:,1),train(:,ftake));
            tTest = test(:,ftake);
            scoreCorr(idx) = corr(tTest*W{idx},testT(:,1));
            error{idx}=testT(:,1)-tTest*W{idx};
            
            %Computing correlationg between features and outputs
            tcorr= corr(A(:,2:9),A(:,10:12));
            fcorr(idx,:) =  tcorr(:); 
        end
        LB  = UB; 
    end
    LB = -1;
   
   
    logicals = (testT(:,2)>1)+1;
    [labels,a,d] = svmpredict(logicals, test(:,2:9), models);
    display(a);
    
    
    
%     for idx = 1:numBins
%         UB = X(idx)+base;
%         logicals = testT(:,2)<UB & testT(:,2)>=LB;
%          if(~sum(logicals)==0)
%              tTest = test(logicals,ftake);
%              scoreCorrN(idx) = corr(tTest*W{idx},testT(logicals,1));
%          end
%         LB  = UB; 
%     end
    
    for idx = 1:numBins
        logicals = labels==idx;
         if(~sum(logicals)==0)
             tTest = test(logicals,ftake);
             scoreCorrN(idx) = corr(tTest*W{idx},testT(logicals,1));
         end
    end
    
    display(X);
    display(fcorr);
    display(scoreCorr);
    display(scoreCorrN);
    display(error);
end