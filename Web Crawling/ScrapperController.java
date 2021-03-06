import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class ScrapperController {

    public static void main(String[] args) throws Exception {

    	 String crawlStorage = "C:\\Users\\Ikshita\\Desktop\\hw2ir\\data";
    	 int numberOfCrawlers = 1;
         CrawlConfig config = new CrawlConfig();
         config.setCrawlStorageFolder(crawlStorage);
         config.setUserAgentString("Ikshita Mishra");
         config.setPolitenessDelay(1000);
         config.setMaxDepthOfCrawling(16);
         config.setMaxPagesToFetch(20000);
         config.setResumableCrawling(false);
         config.setIncludeBinaryContentInCrawling(Boolean.TRUE);
         PageFetcher pageFetcher = new PageFetcher(config);
         RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
         RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
         CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
         controller.addSeed("https://nypost.com/");
         controller.start(Scrapper.class, numberOfCrawlers);

    }

}
