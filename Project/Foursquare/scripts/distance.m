% Program to calculate surface distance between two points
% on Earth given the latitude and longitude

disp('Input the latitude and longitude of each point as prompted')
disp('Lat and Long should be in degrees (convert minutes and seconds)')
disp('Use negative degrees for west and south')

lat1 = input ('Latitude point 1: ');
lon1 = input ('Longitude point 1: ');
lat2 = input ('Latitude point 2: ');
lon2 = input ('Longitude point 2: ');

%Convert to radians

latrad1 = lat1*pi/180;
lonrad1 = lon1*pi/180;
latrad2 = lat2*pi/180;
lonrad2 = lon2*pi/180;

londif = abs(lonrad2-lonrad1);

raddis = acos(sin(latrad2)*sin(latrad1)+ cos(latrad2)*cos(latrad1)*cos(londif));

nautdis = raddis * 3437.74677;
statdis = nautdis * 1.1507794;
stdiskm = nautdis * 1.852;
disp(' ')
fprintf('Distance in radians: = %7.4f \n', raddis);
fprintf('Distance in nautical miles: = %7.2f \n', nautdis);
fprintf('Distance in statute miles: = %7.2f \n', statdis);
fprintf('Distance in kilometers: = %7.2f \n', stdiskm);