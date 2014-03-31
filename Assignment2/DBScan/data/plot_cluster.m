a = importdata('data.out',' ');
cid = unique(a(:,3));
cmap = hsv(length(cid));
figure(1);
hold on;
for id = cid'
    if(id==0)
        plot(a(a(:,3)==id,1),a(a(:,3)==id,2),'kx','MarkerFaceColor', [0 0 0]);
    else
        plot(a(a(:,3)==id,1),a(a(:,3)==id,2),'ko','MarkerFaceColor', cmap(id+1,:)');
    end
    
end

hold off;