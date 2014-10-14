(ns sample.core
  (:require [aggregate.core :as agg]
            [sample.testsupport :refer :all]
            [sample.h2 :as h2]))


(def schema
  [:customer [(id-column)
              [:name "varchar(30)"]]
   :person [(id-column)
            [:name "varchar(30)"]]
   :project [(id-column)
             [:name "varchar(30)"]
             (fk-column :person :manager_id false)
             (fk-column :customer false)]
   :task [(id-column)
          [:desc "varchar(50)"]
          [:effort "integer"]
          (fk-column :project false)
          (fk-column :person :assignee_id false)]
   :person_project [(fk-column :project false)
                    (fk-column :person false)]])


#_ (h2/start-db)

#_ (create-schema! @h2/db-con schema)


(def er-config
  (agg/make-er-config (agg/entity :person
                                  (agg/->n :tasks :task {:fk-kw :assignee_id}))
                      (agg/entity :task)))


#_ (agg/save! er-config @h2/db-con :person {:name "Donald"
                                            :tasks
                                            [{:desc "T1" :effort 10}
                                             {:desc "T2" :effort 100}]})

#_ (dump-table @h2/db-con :task [[:desc 20] [:effort 4] [:assignee_id 4]])
