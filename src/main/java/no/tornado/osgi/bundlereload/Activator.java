package no.tornado.osgi.bundlereload;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class Activator implements BundleActivator, Runnable {
	private WatchService watcher;
	private BundleContext context;

	public void start(BundleContext context) throws Exception {
		this.context = context;

		String reloadDirsString = System.getProperty("bundle.reload.dirs");
		if (reloadDirsString == null) return;
		System.out.println("Live Bundle Reloader configured with bundle.reload.dirs=" + reloadDirsString);

		watcher = FileSystems.getDefault().newWatchService();

		for (String dir : reloadDirsString.split(",")) {
			Path watchFolder = Paths.get(dir);
			System.out.println(String.format("Watching folder %s for bundle reload...", watchFolder));
			watchFolder.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		}

		new Thread(this).start();
	}

	public void stop(BundleContext context) throws Exception {
		if (watcher != null) watcher.close();
	}

	public void run() {
		boolean keepRunning = true;
		while (keepRunning) {
			keepRunning = context.getBundle().getState() == Bundle.ACTIVE;

			try {
				WatchKey key = watcher.take();
				for (WatchEvent event : key.pollEvents()) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						Path modifiedPath = (Path) event.context();
						String filename = modifiedPath.getFileName().toString();

						for (Bundle bundle : context.getBundles()) {
							String location = bundle.getLocation();
							if (location.startsWith("file:")) {
								String bundleFilename = Paths.get(location.substring("file:".length())).getFileName().toString();
								if (bundleFilename.equals(filename)) {
									System.out.println(String.format("Reloading %s...", bundle));
									bundle.update();
									break;
								}
							}
						}
					}
				}
			} catch (ClosedWatchServiceException ex) {
				keepRunning = false;
			} catch (Exception ex) {
				System.out.println("Watcher failed. Sleeping for 5 seconds and retrying...");
				ex.printStackTrace();
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}