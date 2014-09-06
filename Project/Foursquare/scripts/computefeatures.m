function [] = computefeatures()
    radius=5;
    [tvenues,cvenues,ccheckincnt,tcheckincnt]=commonvenues();
    
%     [invAvgDistance,invWeightDistance,cntCVenues] = localityInfluence(tvenues,cvenues,ccheckincnt,radius);     
%     [avgRatings,cntSCompetitors,cntWCompetitors] = competetiveness(tvenues,radius);
%     [crowdiness,attrCoefficient,checkin_score,NoOfNeighbouringVenues]=crowdiness_attractions(tvenues,cvenues,ccheckincnt,tcheckincnt,radius);
%    
%     [ratings] = scoringfields(tvenues); 
%     
    M = [tvenues crowdiness NoOfNeighbouringVenues invAvgDistance invWeightDistance cntCVenues avgRatings cntSCompetitors cntWCompetitors attrCoefficient ratings checkin_score];
    save('dataset','M');
end

function [venues,cvenues,ccheckincnt,tcheckincnt] = commonvenues()
    cfile = fullfile(pwd,'..','data','fcheckins');
    fcheckins = load(cfile);
    fcheckins = fcheckins.checkins;
    [uvenues,~,idx] = unique(fcheckins(:,3));
    counts = accumarray(idx(:),1,[],@sum); 
    threshold=quantile(counts,0.995);
    cvenues = uvenues(counts>threshold);
    ccheckincnt = counts(counts>threshold); 
    venues = uvenues(counts<=threshold);
    tcheckincnt = counts(counts<=threshold); 
end





