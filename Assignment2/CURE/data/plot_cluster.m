a = importdata('data.out',' ');
mean = importdata('mean.out',' ');
cid = unique(a(:,3));
color = {'r+','b+','g+','c+','m+'};
figure(1);
hold on;
for id = cid'
    plot(a(a(:,3)==id,1),a(a(:,3)==id,2),color{id});
end

colori = 'ko';
for m = mean'
    plot(m(1),m(2),colori);
end
hold off;