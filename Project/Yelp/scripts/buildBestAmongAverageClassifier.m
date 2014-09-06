function [] = buildBestAmongAverageClassifier(gamma,nu)
    load('dataset19Apr.mat');
    rng(pi);
    binIndex = 11;
    remove = 0;
    [~,idx] = sort(M(:,binIndex),'ascend');
    M=M(idx(remove+1:end),:);
 
    numBins = 3;
%     [N,X]= hist(M(:,binIndex),numBins);
%     display(N);
%     base = X(1);
    p=fliplr(ones(1,numBins)./(1:numBins));
    X=quantile(M(:,binIndex),p);
    display(X);
%     X = [0.5 1 max(M(:,binIndex))];
    base=0;
    
    numSamples = size(M,1);
    labels  = ones(numSamples,1);
    
    LB =-1;
    for idx = 1:numBins
    UB = X(idx)+base;
        logicals = M(:,binIndex)<=UB & M(:,binIndex)>LB;
        if(~sum(logicals)==0)
            labels(logicals)=idx;
        end
        LB  = UB; 
    end
%     M(:,2:9) = zscore(M(:,2:9));
    
    trainSamples = floor(0.70*numSamples);
    test = ones(numSamples-trainSamples,9);
    
    ridx = randperm(numSamples);
    test(:,2:end)= M(ridx(trainSamples+1:numSamples),2:9);
    testT = M(ridx(trainSamples+1:numSamples),10:12);
    M=M(ridx(1:trainSamples),:);
    
%     svmtraining
    svmoptions = ['-s 1 -t 2 -g ' num2str(gamma) ' -n ' num2str(nu)];
    display(svmoptions);
    models = svmtrain(labels(ridx(1:trainSamples)),M(:,2:9),svmoptions);
    
%    svmprediction
    [~,accuracy,~] = svmpredict(labels(ridx(trainSamples+1:numSamples)), test(:,2:9), models);
    display(accuracy);

end