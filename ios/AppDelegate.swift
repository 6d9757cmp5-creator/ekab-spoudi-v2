import UIKit
import WebKit

@main
final class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let window = UIWindow(frame: UIScreen.main.bounds)
        window.rootViewController = V2BrowserViewController()
        window.makeKeyAndVisible()
        self.window = window
        return true
    }
}

/// The native iOS entry point of the independent EKAB Spoudi V2 project.
/// The same V2 site runs in Safari and in the iPhone app; this does NOT use the
/// other EKAB Spoudi app, its Firebase account, or its stored progress.
final class V2BrowserViewController: UIViewController, WKNavigationDelegate {
    private let siteURL = URL(string: "https://6d9757cmp5-creator.github.io/ekab-spoudi-v2/?v=20261029-ios-startup-fix2")!
    private let webView = WKWebView(frame: .zero, configuration: WKWebViewConfiguration())
    private var offlineMode = false

    override func loadView() {
        view = webView
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        webView.navigationDelegate = self
        webView.isOpaque = false
        webView.backgroundColor = UIColor(red: 14/255, green: 26/255, blue: 34/255, alpha: 1)
        webView.scrollView.backgroundColor = webView.backgroundColor
        webView.scrollView.contentInsetAdjustmentBehavior = .automatic
        webView.allowsBackForwardNavigationGestures = true
        webView.load(URLRequest(url: siteURL, cachePolicy: .reloadIgnoringLocalCacheData, timeoutInterval: 25))
    }

    override var preferredStatusBarStyle: UIStatusBarStyle { .lightContent }

    private func loadBundledPreview() {
        guard !offlineMode else { return }
        offlineMode = true
        guard let html = Bundle.main.url(forResource: "index", withExtension: "html", subdirectory: "WebAssets") else {
            let alert = UIAlertController(title: "ΕΚΑΒ Σπουδή V2", message: "Δεν υπάρχει σύνδεση και λείπει το τοπικό αντίγραφο της εφαρμογής.", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "Εντάξει", style: .default))
            present(alert, animated: true)
            return
        }
        webView.loadFileURL(html, allowingReadAccessTo: html.deletingLastPathComponent())
    }

    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        loadBundledPreview()
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        loadBundledPreview()
    }

    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction,
                 decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        guard let url = navigationAction.request.url else {
            decisionHandler(.cancel)
            return
        }
        if url.scheme == "file" || url.host == siteURL.host || url.scheme == "about" {
            decisionHandler(.allow)
        } else if navigationAction.navigationType == .linkActivated,
                  url.scheme == "https" || url.scheme == "http" {
            UIApplication.shared.open(url)
            decisionHandler(.cancel)
        } else {
            decisionHandler(.cancel)
        }
    }
}