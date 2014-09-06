
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