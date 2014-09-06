function [] = cluster()
   load('dataset.mat');
   ftake = [3 5 7];k=5;
   IDX = kmeans(M(:,ftake),k);
   scatter3(M(:,ftake(1)),M(:,ftake(2)),M(:,ftake(3)),10,IDX);
end