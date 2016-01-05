#!/usr/bin/env boot

(set-env!
 :source-paths #{"src" "test"}
 :dependencies '[[c3p0/c3p0 "0.9.1.2"]
                 [org.clojure/java.jdbc "0.4.2"]

                 [adzerk/boot-test "1.1.0" :scope "test"]
                 [criterium "0.4.3" :scope "test"]
                 [adzerk/bootlaces "0.1.13" :scope "test"]

                 [org.xerial/sqlite-jdbc "3.8.11.2" :scope "test"]
                 [mysql/mysql-connector-java "5.1.38" :scope "test"]
                 [com.h2database/h2 "1.4.187" :scope "test"]
                 [org.postgresql/postgresql "9.4-1206-jdbc42" :scope "dev"]])

(require '[adzerk.boot-test :refer (test)]
         '[adzerk.bootlaces :refer :all])

(def +version+ "0.4.2")
(bootlaces! +version+)

(task-options!
 push {:gpg-sign false}
 pom {:project  'sql.dsl/korma
      :version  +version+
      :url      "https://github.com/gfZeng/Korma"
      :scm      {:url "https://github.com/gfZeng/Korma"}
      :license  {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})
