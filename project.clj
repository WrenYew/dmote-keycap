(defproject dmote-keycap "0.4.0-SNAPSHOT"
  :description "Printable keycap models for mechanical keyboards"
  :url "https://viktor.eikman.se/article/the-dmote/"
  :license {:name "EPL-2.0 OR GPL-3.0-or-later"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "1.0.194"]
                 [environ "1.1.0"]
                 [hiccup "2.0.0-alpha2"]
                 [scad-app "0.2.0"]
                 [scad-clj "0.5.3"]
                 [scad-tarmi "0.4.0"]]
  :plugins [[lein-environ "1.1.0"]]
  :main ^:skip-aot dmote-keycap.core)
