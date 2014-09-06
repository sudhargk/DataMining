function[error1,error2] = model()
    load('dataset1km.mat');
    M=M(~isnan(M(:,6)),:);
    [~,idx] = sort(M(:,12),'descend');
    M=M(idx(1:4000),:);
    
    numSamples = length(M);
    
    trainSamples = ceil(0.70*numSamples);
    idx = randperm(numSamples);
     M = M(idx(1:4000),:);
    train = ones(trainSamples,10);
    test = ones(numSamples-trainSamples,10);
    
    train(:,2:10)=M(1:trainSamples,2:10);
    trainT = M(1:trainSamples,11:13);
   
    test(:,2:10)=M((trainSamples+1):numSamples,2:10);
    testT = M(trainSamples+1:numSamples,11:13);
    
    model1 = regress(trainT(:,1),train);
    model2 = regress(trainT(:,2),train);
    model3 = regress(trainT(:,3),train);
    
    out1 = test*model1;
    out2 = test*model2;
    out3 = test*model3;
    
    figure();
    plot(testT(:,1),out1,'r+'),title('Predicted ratings vs Actual ratings'),hold on;
    plot([testT(:,1);out1],[testT(:,1);out1],'b');
    hold off;
    xlabel('Actual ratings');
    ylabel('Predicted ratings');
    
    figure();
    
    plot(testT(:,2),out2,'b+'),title('Predicted attraction coeficient vs actual attraction coeficient'),hold on;
    plot([testT(:,2);out2],[testT(:,2);out2],'b');
    hold off;
    xlabel('Actual');
    ylabel('Predicted');
    
    figure();
    
    plot(testT(:,3),out3,'g+'),title('Predicted checkins  vs actual checkins'),hold on;
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