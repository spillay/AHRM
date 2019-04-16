export const serverConfig = {
    elasticsearch: "http://sp008.hopto.org:9200/",
    logging: "trace"
};
export const kibanaConfig = { 
    TimeSeriesURL: "http://localhost:5601/app/kibana#/dashboard/ada0a340-9342-11e7-9bc4-9f08c5d9f913?embed=true&_g=(refreshInterval%3A(display%3AOff%2Cpause%3A!f%2Cvalue%3A0)%2Ctime%3A(from%3Anow-1h%2Cmode%3Aquick%2Cto%3Anow))",
    DeptGenderURL: "http://localhost:5601/app/kibana#/dashboard/99941150-933f-11e7-9bc4-9f08c5d9f913?embed=true&_g=(refreshInterval%3A('%24%24hashKey'%3A'object%3A329'%2Cdisplay%3AOff%2Cpause%3A!f%2Csection%3A0%2Cvalue%3A0)%2Ctime%3A(from%3Anow%2Fw%2Cmode%3Aquick%2Cto%3Anow%2Fw))",
    DeptIndicatorURL: "http://localhost:5601/app/kibana#/dashboard/79d248a0-933a-11e7-9bc4-9f08c5d9f913?embed=true&_g=(refreshInterval%3A('%24%24hashKey'%3A'object%3A329'%2Cdisplay%3AOff%2Cpause%3A!f%2Csection%3A0%2Cvalue%3A0)%2Ctime%3A(from%3Anow%2Fw%2Cmode%3Aquick%2Cto%3Anow%2Fw))",
    SimpleAnalysisURL: "http://localhost:5601/app/kibana#/dashboard/f4adf8c0-925f-11e7-94ae-d9e429781847?embed=true&_g=(refreshInterval%3A(display%3AOff%2Cpause%3A!f%2Cvalue%3A0)%2Ctime%3A(from%3Anow%2Fw%2Cmode%3Aquick%2Cto%3Anow%2Fw))"
};
//\"must\" :[{ \"term\": { \"model.senderGeoIP.country_name.keyword\": \"##country##\" }}]
export const db = {
    Topics: '[\
        {"name": "Country",\
        "map":"model.senderGeoInfo.country_name.keyword",\
        "queries":[\
            {\
                "queryType":"Terms",\
                "childfilter":[\
                    {"must" :[{"filter":"{ \\\"term\\\": { \\\"model.senderGeoInfo.country_name.keyword\\\": \\\"##country##\\\" }}"}]}\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","data","sub","buckets"]\
            }\
        ]},\
        {"name": "Department",\
        "map":"department.keyword",\
        "queries":[\
            {\
                "queryType":"Terms",\
                "childfilter":[\
                    {"must" :[{"filter":"{ \\\"term\\\": { \\\"department.keyword\\\": \\\"##department##\\\" }}"}]}\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","data","sub","buckets"]\
            }\
        ]},\
        {"name": "Products",\
        "map":"product.keyword",\
        "queries":[\
            {\
                "queryType":"Terms",\
                "childfilter":[\
                    {"must" :[{"filter":"{ \\\"term\\\": { \\\"product.keyword\\\": \\\"##products##\\\" }}"}]}\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","data","sub","buckets"]\
            }\
        ]},\
        {"name": "Indicator","map":"deception.indicator.keyword",\
        "queries":[\
            {\
                "queryType":"Terms",\
                "childfilter":[\
                    {"must" :[{"filter":"{ \\\"term\\\": { \\\"deception.indicator.keyword\\\": \\\"##indicator##\\\" }}"}]}\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","data","sub","buckets"]\
            },\
            {\
                "queryType":"ConstantQuery",\
                "childfilter":[],\
                "Params":[],\
                "DataPosition":["hits","hits"]\
            }\
        ]},\
        {"name": "Emotions","map":"prime.keyword",\
        "queries":[\
            {\
                "queryType":"Terms",\
                "childfilter":[\
                    {"must" :[{"filter":"{ \\\"term\\\": { \\\"prime.keyword\\\": \\\"##prime##\\\" }}"}]}\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","data","sub","buckets"]\
            },\
            {\
                "queryType":"ConstantQuery",\
                "childfilter":[],\
                "Params":[],\
                "DataPosition":["hits","hits"]\
            }\
        ]},\
        {"name": "Prime","map":"prime.keyword",\
        "queries":[\
            {\
                "queryType":"Terms",\
                "childfilter":[\
                    {"must" :[{"filter":"{ \\\"term\\\": { \\\"prime.keyword\\\": \\\"##prime##\\\" }}"}]}\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","data","sub","buckets"]\
            },\
            {\
                "queryType":"Series",\
                "childfilter":[\
                ],\
                "Params":[],\
                "DataPosition":["aggregations","date","buckets"]\
            }\
        ]}\
    ]'
};
export const settings = {
    server: '{"host":"sp008.hopto.org:9200","log":"trace"}',
    Index:'"index": "logstash-emails"',
    Index2:'"index": "email-outbox"',
    BasicTemplate:'"body": {\
        "size":0,\
        "aggs" : {\
            "data" : {\
                ##FILTER##,\
                "aggs" : {\
                    "sub" : { ##SUB## }\
                }\
            }\
        }\
      }',
      SeriesTemplate:'"body": {\
        "size":0,\
        "aggs" : {\
            "date": {\
                "date_histogram": {\
                  "field": "##DATEFIELD##",\
                  "interval": "##INTERVAL##",\
                  "time_zone": "America/New_York",\
                  "min_doc_count": 1\
                },\
                "aggs": {\
                  "prime": {\
                    "terms": {\
                      "field": "##MAP##",\
                      "size": ##SIZE##,\
                      "order": {\
                        "_count": "desc"\
                      }\
                    }\
                  }\
                }\
              }\
        },\
        "query": {\
            "bool": {\
              "must": [\
                {\
                  "match_all": {}\
                },\
                {\
                  "range": {\
                    "model.date": {\
                        "gte": "##START##",\
                        "lte": "##END##",\
                        "format": "MM-dd-yyyy||MM/dd/yyyy"\
                    }\
                  }\
                }\
              ],\
              "filter": [],\
              "should": [],\
              "must_not": []\
            }\
          }\
      }',
      FilterTemplate:'\
      "filter":{\
        "bool":{\
            "must" :[\
                ##MUSTFILTER##\
            ],\
            "must_not":[\
                ##MUSTNOTFILTER##\
            ]\
        }\
       }\
      ',
      TimeFilter:'\
                { "range" : {\
                    "model.date" : {\
                            "gte": "##START##",\
                            "lte": "##END##",\
                            "format": "MM-dd-yyyy||MM/dd/yyyy"\
                    }\
                  }\
                }',
      ConstantQuery:'"body": {\
            "from" : ##PAGESTART##, "size" : ##PAGEEND##,\
            "query": {\
                "constant_score" : {\
                    ##FILTER##\
                    "boost" : 1.2\
                }\
            }\
        }\
      '
};
export const elasticsearch ={
    server: '{"host":"sp008.hopto.org:9200","log":"trace"}',
    productTruth: '{ \
        "index": "logstash-emails", \
        "body": { \
          "aggs" : { \
            "products" : { \
                "filter" : { "term": { "deception.indicator.keyword": "Truth" } }, \
                "aggs" : { \
                    "sub" : { "terms" : { "field" : "model.products.keyword" } } \
                } \
            } \
          } \
        } \
    }',
    productDeception: '{ \
        "index": "logstash-emails", \
        "body": { \
          "aggs" : { \
            "products" : { \
                "filter" : { "term": { "deception.indicator.keyword": "Deceptive" } }, \
                "aggs" : { \
                    "sub" : { "terms" : { "field" : "model.products.keyword" } } \
                } \
            } \
          } \
        } \
      }',
      countryDist: '{ \
        "index": "logstash-emails", \
        "body": { \
          "aggs" : { \
            "data" : { "terms" : { "field" : "model.senderGeoIP.country_name.keyword" } }\
          } \
        } \
      }',
      DeceptiveCountry: '{         "index": "logstash-emails",         "body": {           "size":0,"aggs" : {             "data" : {                 "filter" : { "term": { "deception.indicator.keyword": "Deceptive" } },                 "aggs" : {                     "sub" : { "terms" : { "field" : "model.senderGeoIP.country_name.keyword" } }                 }             }           }         }       }',
      products: '{         "index": "logstash-emails",         "body": {\
      "size": 0,\
      "aggs" : {\
            "products" : {\
                "terms" : {\
                    "field" : "model.products.keyword"\
                }\
            }\
        }}}',
      CountrybyIndicator: '{         "index": "logstash-emails",         "body": {\
        "size":0,\
        "aggs" : {\
            "data" : {\
                "filter" : {\
                  "bool":{\
                        "must" :[\
                          { "term": { "model.senderGeoIP.country_name.keyword": "##country##" }}\
                        ]\
                  }\
                },\
                "aggs" : {\
                    "sub" : { "terms" : { "field" : "deception.indicator.keyword" } }\
                }\
            }\
        }\
      }}',
      testDataGrid:'{"index": "logstash-emails","body": {            "from" : ##PAGESTART##, "size" : ##PAGESIZE##,            "query": {                "constant_score" : {                          "filter":{        "bool":{            "must" :[                                { "range" : {                    "timestamp" : {                            "gte": "01/01/2017",                            "lte": "2018",                            "format": "dd/MM/yyyy||yyyy"                    }                  }                },{ "term": { "model.senderGeoIP.country_name.keyword": "\\\"United Kingdom\\\"" }},{ "term": { "deception.indicator.keyword": "Truth" }}            ],            "must_not":[                            ]        }       }      ,                    "boost" : 1.2                }            }        }      }'
};
export default db;
/*
CountrybyIndicator: '{         "index": "logstash-emails",         "body": {\
      }}'
*/
