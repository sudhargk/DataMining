%%
%   Locality influence :: 
%       invAvgDistance : Inverse of average distance from the prominent or common places within certain raduis
%       invWeightDistance : Weighted average of distance from prominent or common places within certain radius.
%       cntCVenues : Number of common places in certain radius.
%%

function [invAvgDistance,invWeightDistance,cntCVenues] = localityInfluence()
    vfile = fullfile(pwd,'..','data','data.txt');
    venues = dlmread(vfile,'\t');
    
    invAvgDistance = zeros(length(venues),1);
    invWeightDistance = zeros(length(venues),1);
    cntCVenues = zeros(length(venues),1);
    
    
    pwdist=pdist2(venues(:,2:3),venues(:,2:3),@geodist);
    
    for index = 1:length(venues)
        neighbors = filterDatapoints(venues(:,2:3),tvenues(index,2),tvenues(index,3),radius);
        neighbors = venues(neighbors,1);
        display(index);
        
    end   
    
end