%%
% Competetiveness :
% 	a) Average rating  : Average ratings of venues in surrounding at some radius
% 	b) Count competetive venues : Number of venues with average ratings
% 	greater than equal to 4.5
% 	c) Count venues poor in competitions : Number of venues with average ratings less than equal to 3
%%
function [avgRatings,cntSCompetitors,cntWCompetitors] = competetiveness(tvenues,radius)
    rfile = fullfile(pwd,'..','data','ratings');
    ratings = load(rfile);
    ratings = ratings.ratings;
    
    vfile = fullfile(pwd,'..','data','fvenues');
    venues = load(vfile);
    venues = venues.fvenues; 
    
    tvenues =  venues(ismember(venues(:,1),tvenues),:);
    
    [uvenues,~,idx] = unique(ratings(:,2));
    avgRatingsPerVenues = accumarray(idx,ratings(:,3),[],@mean);
    
    avgRatings = zeros(length(tvenues),1);
    cntSCompetitors = zeros(length(tvenues),1);
    cntWCompetitors = zeros(length(tvenues),1);
    
    for index = 1:length(tvenues)
        neighbors = filterDatapoints(venues(:,2:3),tvenues(index,2),tvenues(index,3),radius);
        neighbors = venues(neighbors,1);
        neighbors = intersect(neighbors,tvenues(:,1));
        neighbors= setdiff(neighbors,tvenues(index,1));
        
        display(index);
        avgRatings(index) = mean(avgRatingsPerVenues(ismember(uvenues,neighbors)));
        cntSCompetitors(index) = sum(avgRatingsPerVenues(ismember(uvenues,neighbors))>3.5);
        cntWCompetitors(index) = sum(avgRatingsPerVenues(ismember(uvenues,neighbors))<2.5);
    end
end
