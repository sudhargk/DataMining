%%
%   Locality influence :: 
%       invAvgDistance : Inverse of average distance from the prominent or common places within certain raduis
%       invWeightDistance : Weighted average of distance from prominent or common places within certain radius.
%       cntCVenues : Number of common places in certain radius.
%%

function [invAvgDistance,invWeightDistance,cntCVenues] = localityInfluence(tvenues,cvenues,ccheckincnt,radius)
    vfile = fullfile(pwd,'..','data','fvenues');
    venues = load(vfile);
    venues = venues.fvenues;
    
    invAvgDistance = zeros(length(tvenues),1);
    invWeightDistance = zeros(length(tvenues),1);
    cntCVenues = zeros(length(tvenues),1);
    
    tvenues =  venues(ismember(venues(:,1),tvenues),:);
    cvenues =  venues(ismember(venues(:,1),cvenues),:);
    
    pwdist=pdist2(tvenues(:,2:3),cvenues(:,2:3),@geodist);
    ccheckincnt = 1./ccheckincnt;
    weight = mat2gray(ccheckincnt);
    
    for index = 1:length(tvenues)
        neighbors = filterDatapoints(venues(:,2:3),tvenues(index,2),tvenues(index,3),radius);
        neighbors = venues(neighbors,1);
        ncvenues = intersect(neighbors,cvenues(:,1));
        display(index);
        if(length(ncvenues)>=1)
            ntvenues= setdiff(neighbors,ncvenues);
            dist = pwdist(ismember(tvenues(:,1),ntvenues),ismember(cvenues(:,1),ncvenues));
            weightDist = dist*weight(ismember(cvenues(:,1),ncvenues));
            cntCVenues(index) = length(ncvenues);
            invAvgDistance(index)=1/mean2(dist);
            invWeightDistance(index)=1/mean(weightDist);
        end
    end   
    
end