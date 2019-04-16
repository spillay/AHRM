import moment from 'moment'

export default class DateHelper {
    constructor(pivotDate) {
      this.pivotDate = pivotDate;
    }
  
    getStr(dte,format){
        return moment(dte).format(format)
    }
    get24HourRange(){
        //var dt = Date.parse(this.pivotDate);
        //var dte = new Date()
        var start = moment(this.pivotDate, "MM-DD-YYYY").subtract(1,"d");
        var end = moment(this.pivotDate, "MM-DD-YYYY");
        var res = {
            "start": start.format("MM-DD-YYYY"),
            "end":end.format("MM-DD-YYYY")
        }
        return res
    }
    getWeekRange(){
        //var dt = Date.parse(this.pivotDate);
        //var dte = new Date()
        var start = moment(this.pivotDate, "MM-DD-YYYY").subtract(7,"d");
        var end = moment(this.pivotDate, "MM-DD-YYYY");
        var res = {
            "start": start.format("MM-DD-YYYY"),
            "end":end.format("MM-DD-YYYY")
        }
        return res
    }
    getMonthRange(){
        //var dt = Date.parse(this.pivotDate);
        //var dte = new Date()
        var start = moment(this.pivotDate, "MM-DD-YYYY").subtract(1,"months");
        var end = moment(this.pivotDate, "MM-DD-YYYY");
        var res = {
            "start": start.format("MM-DD-YYYY"),
            "end":end.format("MM-DD-YYYY")
        }
        return res
    }
    getYearRange(){
        //var dt = Date.parse(this.pivotDate);
        //var dte = new Date()
        var start = moment(this.pivotDate, "MM-DD-YYYY").subtract(1,"years");
        var end = moment(this.pivotDate, "MM-DD-YYYY");
        var res = {
            "start": start.format("MM-DD-YYYY"),
            "end":end.format("MM-DD-YYYY")
        }
        return res
    }
    getSeries(query,interval,top){
        var range = null;
        switch(interval){
            case "1h":
                range = this.get24HourRange()
                break;
            case "1d":
                range = this.getWeekRange()
                break;
            case "1w":
                range = this.getMonthRange()
                break;
            case "1M":
                range = this.getYearRange()
                break;
        }
        query.addTimeSeriesParams("model.date", interval, top, range.start, range.end);
        return query.getFullQuery();
    }
  }
  