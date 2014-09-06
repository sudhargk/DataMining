%%
%  A) Crowdiness or Popularity: Number of  checkins in surrounding at some radius.
%  B) Attraction Coeficient : 
% 	a) Ratio of # of checkins in neighborhood to # of checkins in near by common place.
%  C) Checkin Score : Number of checkins in a venue per average checkins in
%  the neigborhod.
%%
function [crowdiness,attrCoefficient,score_checkins,noOfNeighbouringVenues] = crowdiness_attractions(tvenues,cvenues,ccheckincnt,tcheckincnt,radius)
    vfile = fullfile(pwd,'..','data','fvenues');
    venues = load(vfile);
    venues = venues.fvenues;
    
    attrCoefficient = zeros(length(tvenues),1);
    crowdiness = zeros(length(tvenues),1);
    score_checkins = zeros(length(tvenues),1);
    noOfNeighbouringVenues = zeros(length(tvenues),1);

    tvenues =  venues(ismember(venues(:,1),tvenues),:);
       
    for index = 1:length(tvenues)
        neighbors = filterDatapoints(venues(:,2:3),tvenues(index,2),tvenues(index,3),radius);
        neighbors = venues(neighbors,1);
        ncvenues = intersect(neighbors,cvenues);
        display(index);
        ntvenues= intersect(neighbors,tvenues(:,1));
        noOfNeighbouringVenues(index)=length(ntvenues);
        crowdiness(index)= sum(tcheckincnt(ismember(tvenues(:,1),ntvenues)));
        score_checkins(index) = tcheckincnt(index)/mean(tcheckincnt(ismember(tvenues(:,1),ntvenues)));
        if(length(ncvenues)>=1)
            crowdiness(index)= crowdiness(index) + sum(ccheckincnt(ismember(cvenues(:,1),ncvenues)));
            attrCoefficient(index)=mean(tcheckincnt(ismember(tvenues(:,1),ntvenues)))/mean(ccheckincnt(ismember(cvenues(:,1),ncvenues)));
        end
        
    end

end