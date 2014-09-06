function forPresentations()
%%Plotting markers of checkins - Popularity in different spatial
%%coordinates

    A = importdata('data.txt','\t');
    load('dataset19Apr.mat');
    logicals  = ismember(A(:,1),M(:,1));
    A = A(logicals,:);
    colors = colormap(gray);
    numBins = 10;
    colors = colors(round(linspace(5,54,numBins)),:);
    binIndex = 9;
        [N,X]= hist(M(:,binIndex),numBins);
        display(N);
        base = X(1);
%         p=fliplr(ones(1,numBins)./(1:numBins));
%         X=quantile(M(:,binIndex),p);
%         display(X);
    %     X = [0.5 1 max(M(:,binIndex))];
     base=0;
   
    minimum =2;ratio = 5;
    hold on;
    flip = false;
    if flip
         LB =X(numBins-1);  
         for idx = 1:numBins
            UB = X(idx)+base;
            logicals = M(:,binIndex)<=UB & M(:,binIndex)>LB;
            if(~sum(logicals)==0)
                plot(A(logicals,2),A(logicals,3),'.','MarkerSize',minimum+(idx*ratio),'MarkerEdgeColor',colors(idx,:));
            end
            LB=UB;
        end
    else
        LB = X(numBins-1);
        for idx = numBins:-1:1
            UB = X(idx)+base;
            logicals = M(:,binIndex)<=UB & M(:,binIndex)>LB;
            if(~sum(logicals)==0)
                plot(A(logicals,2),A(logicals,3),'.','MarkerSize',minimum+(idx*ratio),'MarkerEdgeColor',colors(idx,:));
            end
            if(idx>2)
                LB  = X(idx-2); 
            else
                LB=-1;
            end
        end
    end
    hold off;
end