function[ratings] = scoringfields(tvenues)
    rfile = fullfile(pwd,'..','data','ratings');
    ratings = load(rfile);
    ratings = ratings.ratings;
    [uvenues,~,idx] = unique(ratings(:,2));
    ratings = accumarray(idx,ratings(:,3),[],@mean);
    ratings= ratings(ismember(uvenues,tvenues));
end