#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d "{\"tokens\":\"[\"USA\",\"WRAPUP\",\"1-Credit\",\"quality\",\"broad\",\"decline\",\"defaults\",\"soar\",\".\",\"Reuters\",\"English\",\"News\",\"Service\"]\"}" http://localhost:9000/emo2 
