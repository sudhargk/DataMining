function[error1,error2] = model()
    load('dataset.mat');
%     mn = min(M(:,2:8));
%     mx = max(M(:,2:8));
%     diff = mx-mn;
%     
%     M(:,2:8)=bsxfun(@minus,M(:,2:8),mn);
%     M(:,2:8)=bsxfun(@rdivide,M(:,2:8),diff);
%     
     [~,idx] = sort(M(:,10),'ascend');
    
     T=M(idx(1:end),:);
    
%     [~,idx] = sort(M(:,10),'ascend');
%     T=[T; M(idx(1:take),:)];
    M =T;
    
    numSamples = length(M);
  
     take = numSamples-1000:numSamples;
     M = M(take,:);
     numSamples = length(M);
     
    trainSamples = ceil(0.70*numSamples);
    train = ones(trainSamples,8);
    test = ones(numSamples-trainSamples,8);
     idx = randperm(numSamples);
%     idx = 1:numSamples;
    train(:,2:end)=M(idx(1:trainSamples),2:8);
    trainT = M(idx(1:trainSamples),9:11);
   
    test(:,2:end)=M(idx((trainSamples+1):numSamples),2:8);
    testT = M(idx(trainSamples+1:numSamples),9:11);
    ftake = [1 2 3 4 5 7 8];
    model1 = regress(trainT(:,1),train(:,ftake));
    model2 = regress(trainT(:,2),train(:,ftake));
    model3 = regress(trainT(:,3),train(:,ftake));
    
    out1 = test(:,ftake)*model1;
    out2 = test(:,ftake)*model2;
    out3 = test(:,ftake)*model3;
    
    figure();
    plot(testT(:,1),out1,'r+'),title('Predicted checkins vs Actual checkins'),hold on;
    plot([testT(:,1);out1],[testT(:,1);out1],'b');
    hold off;
    xlabel('Actual');
    ylabel('Predicted');
    
    figure();
    
    plot(testT(:,2),out2,'b+'),title('Predicted attraction coeficient vs actual attraction coeficient'),hold on;
    plot([testT(:,2);out2],[testT(:,2);out2],'b');
    hold off;
    xlabel('Actual');
    ylabel('Predicted');
    
    figure();
    
    plot(testT(:,3),out3,'g+'),title('Predicted ratings  vs actual ratings'),hold on;
    plot([testT(:,3);out3],[testT(:,3);out3],'b');
    hold off;
    xlabel('Actual');
    ylabel('Predicted');
    
    
    
    error1 = testT(:,1) - out1;
    error2 = testT(:,2) - out2;
    error3 = testT(:,3) - out3;
    
    display(['MSE (ratings) : ' num2str(mean(error1.*error1))]);
    display(['MSE (attaction) : ' num2str(mean(error2.*error2))])
    display(['MSE (checkins) : ' num2str(mean(error3.*error3))])
end