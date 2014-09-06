function correlationBWClustersLatest()
    load('dataset22Apr.mat');
%      rng(pi);
    numValidation = 1 ;
    binIndex = 12;   numFeatures = 10;
    correlationMethod = 'Pearson'; %'Pearson','Kendall','Pearson'
    modelTech ='regress'; %'regress','svr','rtree'
    
    %----------------Removing TOP K records------------------------
    last = size(M,1);
    range = 1:500;
    [~,idx] = sort(M(:,binIndex),'ascend');
    M=M(idx(range),:);
    %-------------------------------------------------------------------
    
    %-------------------Binning technique-------------------------------
    binIndex=12;     numBins = 4;     binMethod = 'equiFrequent';
    [X,base] = binning(binMethod,M,numBins,binIndex);
    %--------------------------------------------------------------------
    
    
    fcorr = zeros(numBins,(numFeatures-1)*3);
    scoreCorrSep = zeros(numBins,1);
    scoreCorrNearest = zeros(numBins,1);
%     scoreCorrSVM = zeros(numBins,1);
    scoreCorrAll = zeros(numBins,1);
    error = cell(numBins,1);
    model = cell(numBins,1);
    
    for valIndex = 1:numValidation
        %-------------------Splitting Data-------------------------------
        numSamples = size(M,1);
        trainSamples = floor(0.70*numSamples);
        test = ones(numSamples-trainSamples,numFeatures);
        ridx = randperm(numSamples);
        test(:,2:end)= M(ridx((trainSamples+1):numSamples),2:numFeatures);
        testT = M(ridx(trainSamples+1:numSamples),numFeatures+1:end);
        trainS=M(ridx(1:trainSamples),:);
        trainMarker = zeros(trainSamples,1);
        %-----------------------------------------------------------------


        % Allows to set featurre index for with which model needs to built
        ftake = [1 3  5 7 8 9];

        % ---Building Separate Models for each Bins------------------------
        LB = 0;
        for idx = 1:numBins
            UB = X(idx)+base;
            logicals = trainS(:,binIndex)<UB & trainS(:,binIndex)>=LB;
            if(~sum(logicals)==0)
                A = trainS(logicals,:);
                trainSamples = sum(logicals);
                train = ones(trainSamples,numFeatures);

                train(:,2:end)=A(:,2:numFeatures);
                trainT = A(:,numFeatures+1:end);
                trainMarker(logicals) = idx;
                model{idx}  = startTrain(modelTech,train,trainT,ftake);
                tTest = test(:,ftake);
                [~,score] = testModel(modelTech,model{idx},tTest,testT(:,1),correlationMethod);
                scoreCorrAll(idx) = score+scoreCorrAll(idx);
                %--Computing correlationg between features and outputs---
                tcorr= corr(A(:,2:numFeatures),A(:,numFeatures+1:end));
                fcorr(idx,:) =  tcorr(:); 
                %--------------------------------------------------------
            end
            LB  = UB; 
        end
        %-------------------------------------------------------------------
     %----------------------------------SVM-------------------------------
     
%         DC=ones(1,size(test,1)); 
%         AC=ones(1,size(test,1)); 
%         svm_model = svmtrain(trainMarker,trainS(:,ftake),'-s 1 -t 2 -g 0.000002 -n 0.002');
%         
%         LB = 0;
%         for idx = 1:numBins
%              UB = X(idx)+base;
%              logicals = testT(:,2)<UB & testT(:,2)>=LB;
%              AC(logicals)=idx;
%              LB  = UB; 
%         end
%         DC= svmpredict(AC', test(:,ftake), svm_model);
%         for idx =1:numBins
%             logicals = DC==idx;
%             if(~sum(logicals)==0)
%                 [~,scoreCorrSVM(idx)] = testModel(modelTech,model{idx},test(logicals,ftake),testT(logicals,1),correlationMethod);
%             end
%         end
% %         targets=full(ind2vec(AC));    outputs = full(ind2vec(DC));
% %         figure(),plotconfusion(targets,outputs),set(gcf, 'WindowStyle', 'docked');
%     %--------------------------------------------------------------------


        LB = 0;
        count  = zeros(numBins,1);
        for idx = 1:numBins
            UB = X(idx)+base;
            logicals = testT(:,2)<UB & testT(:,2)>=LB;
    %          logicals = test(:,binIndex)<UB & test(:,binIndex)>=LB;
                count(idx)=sum(logicals);
                if(~sum(logicals)==0)
                 [~,score] = testModel(modelTech,model{idx},test(logicals,ftake),testT(logicals,1),correlationMethod);
                 scoreCorrSep(idx) = score +scoreCorrSep(idx);
             end
            LB  = UB; 
        end
        display(count);
    end
%     %-------------------------------Nearest Neigbor-----------------------
    [~,nbr_idx] = pdist2(M(:,2:numFeatures),test(:,2:numFeatures),'euclidean','Smallest',5);
    DC=ones(1,size(test,1)); 
    AC=ones(1,size(test,1)); 
    for index = 1:size(test,1)
        LB = 0;
        count= zeros(numBins,1);
        for idx = 1:numBins
            UB = X(idx)+base;
            logicals = M(nbr_idx(:,index),binIndex)<UB & M(nbr_idx(:,index),binIndex)>=LB;
            count(idx)=sum(logicals);
            LB  = UB; 
        end
        [~,DC(index)] = max(count);
    end
    LB = 0;
    for idx = 1:numBins
         UB = X(idx)+base;
         logicals = testT(:,2)<UB & testT(:,2)>=LB;
         AC(logicals)=idx;
         LB  = UB; 
         logicals = DC==idx;
         if(~sum(logicals)==0)
            [~,score] = testModel(modelTech,model{idx},test(logicals,ftake),testT(logicals,1),correlationMethod);
            scoreCorrNearest(idx) = score + scoreCorrNearest(idx);
         end
    
    end
     targets=full(ind2vec(AC));    outputs = full(ind2vec(DC));
     figure(),plotconfusion(targets,outputs),set(gcf, 'WindowStyle', 'docked');
    %--------------------------------------------------------------------
    
   
    display(X);
    display(fcorr);
    scoreCorrSep=scoreCorrSep/numValidation;
    display(scoreCorrSep);
    scoreCorrAll=scoreCorrAll/numValidation;
    display(scoreCorrAll);
    scoreCorrNearest=scoreCorrNearest;
    display(scoreCorrNearest);
%     display(scoreCorrSVM);
    display(error);
end
 

function [model]= startTrain(technique,train,trainT,ftake)
    switch(technique)
        case 'regress'
            model = regress(trainT(:,1),train(:,ftake));
        case 'rtree'
            model =  RegressionTree.fit(train(:,ftake),trainT(:,1));
        case 'svr'
            svmoptions = '-s 4 -t 2 -g 0.00000001';
            model = svmtrain(trainT(:,1),train(:,ftake),svmoptions);
    end
end

function [output,acc]= testModel(technique,model,test,targets,accmethod)
    switch(technique)
        case 'regress'
            output=test*model;
        case 'rtree'
            output=predict(model,test);
        case 'svr'
            output = svmpredict(targets,test,model);
    end
    acc = corr(output,targets,'type',accmethod);
end

function [X,base]=binning(binMethod,M,numBins,binIndex)
    handle=str2func(binMethod);
    [X,base]=handle(M,numBins,binIndex);
end

function [X,base] = equiInterval(M,numBins,binIndex)
    [N,X]= hist(M(:,binIndex),numBins);
    display(N);
    base = X(1);
end

function [X,base] = equiFrequent(M,numBins,binIndex)
      p=fliplr(ones(1,numBins)./(1:numBins));
      display(['Size : ' num2str(p(1)*size(M,1))])
      X=quantile(M(:,binIndex),p);
      base = 0;
end

function [X,base] = manual(M,numBins,binIndex)
    X = [0.5 1  5 max(M(:,binIndex))];
    base=0;
end
