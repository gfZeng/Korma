(ns korma.plugin.pagination
  (:require [clojure.string :as string]
            [korma.core :refer (limit offset order where)]))

(defprotocol IPaginator
  (-page [this query cursor up|down]))

(defn paginator [query|ent pt]
  (assoc query|ent :paginator pt))

(defrecord OffsetPaginator [page-size order-by]
  IPaginator
  (-page [this query page-no up|down]
    (let [page-size (:limit query page-size)]
      (condp = up|down
        :up (-page this query (inc page-no) nil)
        :down (-page this query (dec page-no) nil)
        (-> query
            (update :order into order-by)
            (offset (* (dec page-no)
                       page-size))
            (limit page-size))))))

(defn offset-paginator [& {:as opts}]
  (map->OffsetPaginator
   (merge {:page-size 10
           :order-by [[:id :ASC]]}
          opts)))

(defn- opposite-order-by [order-by]
  (mapv #(assoc % 1 (get {"asc" :DESC
                          "desc" :ASC}
                         (string/lower-case (name (second %)))))
        order-by))

(defrecord CursorPaginator [page-size order-by]
  IPaginator
  (-page [this query [cur next-cur :as cursor] up|down]
    (condp = up|down
      :up (-page (update this :order-by opposite-order-by)
                 query [cur nil] nil)
      :down (-page this query [next-cur nil] nil)
      (let [paging-filter (reduce (fn [acc [field asc|desc]]
                                    (assoc acc field
                                           [({:ASC '> :DESC '<} asc|desc)
                                            (cur field)]))
                                  {} order-by)]
        (-> query
            (update :order into order-by)
            (limit page-size)
            (where paging-filter))))))

(defonce default-paginator
  (delay (offset-paginator)))

(defn page
  ([query cursor]
   (page query cursor nil))
  ([query cursor up|down]
   (-page (or (:paginator query)
              (:paginator (:ent query))
              @default-paginator)
          query cursor up|down)))
