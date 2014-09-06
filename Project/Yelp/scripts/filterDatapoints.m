
%Returns index of location within certain range (square boundary)
%   A = input array of N x 2 where 
%   cLat = centre latitude cLong = center Longitude
%   distance = distance of the boundary from center in km
%   eg filter(A,37.7756633,-122.4207772,5);
%%
%   idx = filterDatapoints(A(:,2:3),40.7056308,-73.9780035,10);

%%
function[idx] = filterDatapoints(A,cLat,cLong,distance)
    unitLatDist = 112;
    unitLongDist = 78;
    latChange = distance/unitLatDist;
    longChange = distance/unitLongDist;
    fillog = (A(:,1)>cLat-latChange)&(A(:,1)<cLat+latChange)&(A(:,2)>cLong-longChange)&(A(:,2)<cLong+longChange);
    A = 1:length(fillog);
    idx = A(fillog);
end