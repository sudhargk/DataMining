function[fcorr,scoreCorr,Error] = correlationBWClusters()
    load('dataset.mat');
    binIndex = 10;
    remove = 2000;
    rng (pi);
    [~,idx] = sort(M(:,binIndex),'ascend');
    M=M(idx(remove+1:end),:);
    numBins = 2;
    %[N,X]= hist(M(:,binIndex),numBins);
    %base = X(1);
%     p=fliplr(ones(1,numBins)./(1:numBins));
%     X=quantile(M(:,binIndex),p);
    base=0;
    X = [1 max(M(:,binIndex))];
    cols = 21;
    fcorr = zeros(numBins,cols);
    scoreCorr = zeros(numBins,1);
    error = cell(numBins,1);
    LB = 0;
    colors = mat2gray(randn(numBins,3));
%     hold on;
    for idx = 1:numBins
        UB = X(idx)+base;
        logicals = M(:,binIndex)<UB & M(:,binIndex)>=LB;
        if(~sum(logicals)==0)
            A = M(logicals,:);
            numSamples = sum(logicals);
            trainSamples = floor(0.70*numSamples);
            train = ones(trainSamples,8);
            test = ones(numSamples-trainSamples,8);
             
            ridx = randperm(numSamples);
            train(:,2:end)=A(ridx(1:trainSamples),2:8);
            trainT = A(ridx(1:trainSamples),9:11);
            
            test(:,2:end)= A(ridx((trainSamples+1):numSamples),2:8);
            testT = A(ridx(trainSamples+1:numSamples),9:11);
            ftake = 1:8;
            W  = regress(trainT(:,1),train(:,ftake));
        
           % plot3(train(:,ftake(1)),train(:,ftake(2)),train(:,ftake(3)),'o','MarkerFaceColor' ,colors(idx,:),'MarkerSize',5);
           % pause;
            scoreCorr(idx) = corr(test*W,testT(:,1));
%             scoreCorr(idx) = corr(test*W,testT(:,1),'type','Spearman');
            error{idx}=testT(:,1)-test*W;
            %Computing correlationg between features and outputs
            tcorr= corr(A(:,2:8),A(:,9:11));
            fcorr(idx,:) =  tcorr(:); 
        end
        LB  = UB; 
    end
    display(fcorr);
    display(scoreCorr);
    display(error);
%    display(N);
end