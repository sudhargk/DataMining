function [] = computefeatures()
    radius=3;
    [tvenues,cvenues]=commonvenues();
    [f_avgRatings,f_cntSCompetitors,f_cntWCompetitors,f_crowdiness,f_attrCoefficient,f_density,f_cnt_cvenues,f_avg_checkins,f_nbr_avg_checkins,s_checkins,s_norm_checkins,s_ratings] = features(tvenues,cvenues,radius);
    M = [tvenues f_avgRatings,f_cntSCompetitors,f_cntWCompetitors,f_crowdiness,f_attrCoefficient,f_density,f_cnt_cvenues,f_avg_checkins,f_nbr_avg_checkins,s_checkins,s_norm_checkins,s_ratings];
    save('dataset22Apr','M');
end

function [tvenues,cvenues] = commonvenues()
    cfile = fullfile(pwd,'..','data','data.txt');
    data = dlmread(cfile,'\t');
    tvenues = data(data(:,5)==1&data(:,6)~=0,1);
    cvenues = [9557;6893;1799;7057;4067;3648;15482];
end


function [f_avgRatings,f_cntSCompetitors,f_cntWCompetitors,f_crowdiness,f_attrCoefficient,f_density,f_cnt_cvenues,f_avg_checkins,f_nbr_avg_checkins,s_checkins,s_norm_checkins,s_ratings]= features(tvenues,cvenues,radius)
    cfile = fullfile(pwd,'..','data','data.txt');
    venues = dlmread(cfile,'\t');
    
    f_avgRatings = zeros(length(tvenues),1);
    f_cntSCompetitors = zeros(length(tvenues),1);
    f_cntWCompetitors = zeros(length(tvenues),1);
    f_density = zeros(length(tvenues),1);
    f_crowdiness = zeros(length(tvenues),1);
    f_attrCoefficient = zeros(length(tvenues),1);
    f_cnt_cvenues = zeros(length(tvenues),1);
    f_avg_checkins = zeros(length(tvenues),1);
    f_nbr_avg_checkins = zeros(length(tvenues),1);
    tvenues =  venues(ismember(venues(:,1),tvenues),:);
    [~,nbr_idx] = pdist2(tvenues(:,2:3),tvenues(:,2:3),@geodist,'Smallest',10);
    
    s_checkins = tvenues(:,6);
    s_norm_checkins =  zeros(length(tvenues),1);
    s_ratings = tvenues(:,4);
 
    for index = 1:length(tvenues)
       neighbors = filterDatapoints(venues(:,2:3),tvenues(index,2),tvenues(index,3),radius); 
       neighbors= setdiff(neighbors,tvenues(index,1));
       
       ncvenues = intersect(neighbors,cvenues);
       f_cnt_cvenues(index) = length(ncvenues);
       
       %%all venues excluding myself in the radius
       f_crowdiness(index)= sum(venues(ismember(venues(:,1),neighbors),6));
       
       %%only restaurants excluding myself in the radius
       neighbors = intersect(neighbors,tvenues(:,1));
       f_density(index)=length(neighbors);
       if(length(neighbors)>=1)
           s_norm_checkins(index) = tvenues(index,6)/mean(tvenues(ismember(tvenues(:,1),neighbors),6));
           f_avg_checkins(index) = mean(tvenues(ismember(tvenues(:,1),neighbors),6));
           f_attrCoefficient(index) = sum(tvenues(ismember(tvenues(:,1),neighbors),6))/f_crowdiness(index);
           f_avgRatings(index) = mean(tvenues(ismember(tvenues(:,1),neighbors),4));
           f_cntSCompetitors(index) = sum(tvenues(ismember(tvenues(:,1),neighbors),4)>4.0);
           f_cntWCompetitors(index) = sum(tvenues(ismember(tvenues(:,1),neighbors),4)<2.5);
           f_nbr_avg_checkins(index) = mean(tvenues(nbr_idx(:,index),6))/ mean(tvenues(ismember(tvenues(:,1),neighbors),6));
       end
      
       display(index);
    end

end


%%
% Geo distance computation
%   X => 1 x N  vectors
%   Y => m x N  matrix
%   diskm => m x N matrix of geo distance in kilometres
%%
function [diskm] = geodist(X,Y)
    latrad1 = X(1)*pi/180;lonrad1 = X(2)*pi/180;
    latrad2 = Y(:,1)*pi/180;lonrad2 = Y(:,2)*pi/180;
    londif = abs(lonrad2-lonrad1);
    raddis = acos(sin(latrad1)*sin(latrad2)+ cos(latrad1)*cos(latrad2).*cos(londif));
    nautdis = raddis * 3437.74677;
    diskm = nautdis * 1.852;
end

