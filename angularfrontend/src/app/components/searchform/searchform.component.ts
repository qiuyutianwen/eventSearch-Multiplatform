import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-searchform',
  templateUrl: './searchform.component.html',
  styleUrls: ['./searchform.component.css']
})
export class SearchformComponent implements OnInit{

  submitted = false;

  keyword:string = "";
  categories:string[] = ["All", "Music", "Sports", "Arts & Theatre", "Film", "Miscellaneous"];
  category:string = "All";
  distance:string = "10";
  distance_units:string[] = ["Miles", "Kilometers"];
  distance_unit:string="Miles";
  here:string = "true";
  address:string = "";
  selectedResultIndex:number=-1;

  keywordEmpty:boolean = false;
  selectResult:boolean = true;
  noRecordsInfo:string = 'true';
  hasErrorInfo:string = 'true';

  searchResults:any[] = [];
  details:any[] = [];
  favoriteList:any[] = [];
  options:string[] = [];
  hideProgress:boolean = true;
  progressValue:number = 0;
  constructor(private http: HttpClient) { }

  ngOnInit(){
    localStorage.clear();
  }
  onClear(){
    this.keyword = "";
    this.category = "All";
    this.here = "true";
    this.address = "";
    this.submitted=false;
    this.favoriteList=[];
    localStorage.clear();
    this.details=[];
    this.selectedResultIndex=-1;
    this.searchResults=[];
    this.noRecordsInfo='true';
    this.hasErrorInfo='true';
    this.options = [];
    this.hideProgress = true;
    this.progressValue=0;
  }

  onBlurKeyword(){
    console.log("blur");
    if(this.keyword=="") this.keywordEmpty = true;
  }

  onSubmit(){
    this.submitted = true;
    this.hideProgress = false;
    this.progressValue=0;
    let progressIncrease = setInterval(() => {this.progressValue += 10}, 1);
    let params = new HttpParams();
    params = params.append('keyword', this.keyword);
    params = params.append('category', this.category);
    params = params.append('distance', this.distance);
    params = params.append('distance_unit', this.distance_unit);
    params = params.append('here', this.here);
    interface ExampleObject {
      [key: string]: any
    }
    if(this.here === 'true'){
      try{
        this.http.get('https://ipinfo.io?token=9e923be0bf75de').subscribe(res => {
          let ip_data: ExampleObject = res;
          var loc = ip_data.loc;
          console.log(loc);
          params = params.append('loc', loc);
          try{
            this.http.get('https://hw8-eventsearch-nodejs.uw.r.appspot.com/event', {params: params}).subscribe(res=>{
              let result_data: ExampleObject = res;
              if(result_data._embedded === undefined){
                this.noRecordsInfo = 'false';
                this.hasErrorInfo = 'true';
                this.searchResults = [];
              }else{
                let events: any[] = result_data._embedded.events;
                this.searchResults = this.format(events);
                clearInterval(progressIncrease);
                this.hideProgress = true;
                console.log(this.searchResults);
              }
            });
          }catch(error) {
            this.noRecordsInfo = 'true';
            this.hasErrorInfo = 'false';
            this.searchResults = [];
            clearInterval(progressIncrease);
          }
        });
      }catch(error){
        this.noRecordsInfo = 'true';
        this.hasErrorInfo = 'false';
        this.searchResults = [];
        clearInterval(progressIncrease);
      }
    }else{
      params = params.append('address', this.address);
      try{
        this.http.get('https://hw8-eventsearch-nodejs.uw.r.appspot.com/event', {params: params}).subscribe(res=>{
          let result_data: ExampleObject = res;
          if(result_data._embedded === undefined){
            this.noRecordsInfo = 'false';
            this.hasErrorInfo = 'true';
            this.searchResults = [];
          }else{
            let events: any[] = result_data._embedded.events;
            this.searchResults = this.format(events);
            clearInterval(progressIncrease);
            this.hideProgress = true;
            console.log(this.searchResults);
          }
        });
      }catch(error) {
        this.noRecordsInfo = 'true';
        this.hasErrorInfo = 'false';
        this.searchResults = [];
        clearInterval(progressIncrease);
      }
    }

  }

  format(input:any[]):any[]{
    interface ExampleObject {
      [key: string]: any
    }
    let formatted_data:any[] = [];
    for(let i = 0; i < input.length; i++){
      let event:ExampleObject = input[i];
      let formatted_event = {'EventId': 'N/A', 'Date': 'N/A', 'Event': 'N/A', 'Category': 'N/A', 'Venue': 'N/A', 'favorite': false};
      formatted_event.EventId = event.id
      formatted_event.Date = event.dates.start.localDate;
      formatted_event.Event = event['name'];
      let category = 'N/A';
      let category_arr = [];
      if(event['classifications'] !== undefined){
        if(event['classifications'][0] !== undefined) {
          if(event['classifications'][0]['subGenre'] !== undefined) {
            if (event['classifications'][0]['subGenre']['name'] !== undefined && event['classifications'][0]['subGenre']['name'] !== 'Undefined') {
              category_arr.push(event['classifications'][0]['subGenre']['name']);
            }
          }
        }
        if(event['classifications'][0] !== undefined) {
          if(event['classifications'][0]['genre'] !== undefined) {
            if (event['classifications'][0]['genre']['name'] !== undefined && event['classifications'][0]['genre']['name'] !== 'Undefined') {
              category_arr.push(event['classifications'][0]['genre']['name']);
            }
          }
        }
        if(event['classifications'][0] !== undefined) {
          if(event['classifications'][0]['segment'] !== undefined) {
            if (event['classifications'][0]['segment']['name'] !== undefined && event['classifications'][0]['segment']['name'] !== 'Undefined') {
              category_arr.push(event['classifications'][0]['segment']['name']);
            }
          }
        }
        if(event['classifications'][0] !== undefined) {
          if(event['classifications'][0]['subType'] !== undefined){
            if(event['classifications'][0]['subType']['name'] !== undefined && event['classifications'][0]['subType']['name'] !== 'Undefined'){
              category_arr.push(event['classifications'][0]['subType']['name']);
            }
          }
        }
        if(event['classifications'][0] !== undefined) {
          if(event['classifications'][0]['type'] !== undefined){
            if(event['classifications'][0]['type']['name'] !== undefined && event['classifications'][0]['type']['name'] !== 'Undefined'){
              category_arr.push(event['classifications'][0]['type']['name']);
            }
          }
        }
        if(category_arr.length >= 1){
          category = category_arr[0];
        }
        if(category_arr.length >= 2){
          for(let i = 1; i < category_arr.length; i++){
            category += ' | ' + category_arr[i];
          }
        }
        formatted_event.Category = category;
      }

      if(event['_embedded']['venues'] !== undefined){
        if(event['_embedded']['venues'][0] !== undefined){
          if(event['_embedded']['venues'][0]['name'] !== undefined){
            formatted_event.Venue = event['_embedded']['venues'][0]['name'];
          }
        }
      }
      formatted_data[i] = formatted_event;
    }
    let sorted_formatted_data = formatted_data.sort(function(a,b){
      return a.Date >b.Date?1:a.Date <b.Date?-1:0
    })
    return sorted_formatted_data;
  }
  onClickResult(){
    this.selectResult = true;
  }

  onClickFavorite(){
    this.selectResult = false;
    this.favoriteList = this.getAllStorage();
  }
  onChange(keyword:string){
    if(keyword !== null && keyword !== ' '){
      try{
        let suggestions:any[] = [];
        let params = new HttpParams();
        params = params.append('keyword', this.keyword);
        this.http.get('https://hw8-eventsearch-nodejs.uw.r.appspot.com/autocomplete', {params: params}).subscribe(res=> {
          let data = JSON.parse(JSON.stringify(res));
          for(let i = 0; i < data.length; ++i) {
            suggestions.push(data[i]);
          }
        });
        this.options = suggestions;
      }catch(err){
        console.log(err);
      }
    }
  }
  getAllStorage(){
    let list = [];
    let keys = Object.keys(localStorage);
    for(let i = 0; i < keys.length; i++){
      let item = [];
      let key = keys[i];
      if (key != null) {
        item.push(key);
        let value = localStorage.getItem(key);
        if(value != null){
          let result = JSON.parse(value);
          item.push(result.EventId);
          item.push(result.Date);
          item.push(result.Event);
          item.push(result.Category);
          item.push(result.Venue);
        }
      }
      list.push(item);
    }
    console.log(list);
    return list;
  }
}
