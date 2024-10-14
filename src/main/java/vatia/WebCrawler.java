package vatia;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

    private ConcurrentLinkedQueue<String> urlQueue = new ConcurrentLinkedQueue<>();
    private Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private ExecutorService executorService;
    private final int numThreads;
    private static final Logger logger = Logger.getLogger(WebCrawler.class.getName());


    public WebCrawler(int numThreads) {
        this.numThreads = numThreads;
        this.executorService = Executors.newFixedThreadPool(numThreads);
    }

    public void crawl(String url) {
        urlQueue.add(url);
        while (!urlQueue.isEmpty() || !executorService.isShutdown()) {
            if (!urlQueue.isEmpty()) {
                String currentUrl = urlQueue.poll();
                executorService.submit(() -> visitUrl(currentUrl));
            }
        }
    }

    private void visitUrl(String url) {
        if (url == null || visitedUrls.contains(url)){
            return;
        }

        visitedUrls.add(url);
        System.out.println("Visiting: " + url);

        Document content = fetchContentFromUrl(url);

        if (content != null) {
            List<String> newUrls = extractUrls(content);

            for (String newUrl : newUrls) {
                if (!visitedUrls.contains(newUrl)) {
                    urlQueue.add(newUrl);
                }
            }
        }

        saveState();
    }

    private Document fetchContentFromUrl(String url) {
        try {
            Document page = Jsoup.connect(url).get();
            return page;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private List<String> extractUrls(Document content) {
        List<String> newUrls = new ArrayList<>();

        Elements links = content.select("a");

        for (Element link : links) {
            String href = link.attr("href");
            newUrls.add(href);
        }

        return newUrls;
    }

    private void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("crawler_state.dat"))) {
            oos.writeObject(visitedUrls);
            oos.writeObject(new LinkedList<>(urlQueue));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("crawler_state.dat"))) {
            visitedUrls = (Set<String>) ois.readObject();
            urlQueue = new ConcurrentLinkedQueue<>((Queue<String>) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //isPaused.set(true);
    }

    public void resume() {

    }

    public void shutdown() {
        executorService.shutdown();
    }

}
