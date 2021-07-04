import {Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit {
  @Input() results:any[]=[];
  @Output() resultsChange = new EventEmitter<any[]>();
  @Input() noRecordsInfo:string='';
  @Input() hasErrorInfo:string='';
  @Input() details:any[]=[];
  @Output() detailsChange = new EventEmitter<any[]>();
  @Input() lastDetailIndex:number=-1;
  @Output() lastDetailIndexChange = new EventEmitter<number>();

  constructor(private http: HttpClient) { }

  ngOnInit(): void {

  }
  showDetails(i:number){
    this.lastDetailIndex = i;
    this.lastDetailIndexChange.emit(this.lastDetailIndex);
    let event_id = this.results[i]['EventId'];
    let params = new HttpParams();
    params = params.append('id', event_id);
    try{
      this.http.get('https://hw8-eventsearch-nodejs.uw.r.appspot.com/eventdetails', {params: params}).subscribe(res=> {
          console.log('details************');
          console.log(res);
          interface ExampleObject {
            [key: string]: any
          }
          let data: ExampleObject = res;
          // this.details = ['Artist/Team(s)','Venue', 'Time', 'Category', 'Price Range','Ticket Status','Buy Ticket At',
        //                    'Seat Map','artist_name','venue_id', 'event_name', 'favorite'];
          this.details = ['N/A','N/A','N/A','N/A','N/A','N/A','N/A','N/A','N/A','N/A', 'N/A', 'N/A'];
          if(data._embedded !== undefined){
            if(data._embedded.attractions !== undefined){
              let teams:any[] = data._embedded.attractions;
              let teams_data = '';
              for(let i = 0; i < teams.length -1; i++){
                teams_data += teams[i].name + ' | ';
              }
              if(teams.length >= 1){
                teams_data += teams[teams.length -1].name;
              }
              this.details[0] = teams_data;
            }
            if(data._embedded.venues !== undefined){
              if(data._embedded.venues[0] !== undefined){
                this.details[1] = data._embedded.venues[0].name;
                this.details[9] = data._embedded.venues[0].id;
              }
            }
          }
          if(data.dates !== undefined){
            if(data.dates.start !== undefined){
              if(data.dates.start.localDate !== undefined){
                this.details[2] = data.dates.start.localDate;
              }
            }
            if(data.dates.status !== undefined){
              if(data.dates.status.code !== undefined){
                this.details[5] = data.dates.status.code;
              }
            }
          }
          if(this.results[i]['Category'] !== undefined){
            this.details[3] = this.results[i]['Category'];
          }
          if(this.results[i]['Event'] !== undefined){
            this.details[10] = this.results[i]['Event'];
          }
        if(this.results[i]['favorite'] !== undefined){
          this.details[11] = this.results[i]['favorite'];
        }
          if(data.priceRanges !== undefined){
            if(data.priceRanges[0] !== undefined){
              let price_range = '';
              price_range += data.priceRanges[0].min + ' - ' + data.priceRanges[0].max + ' ' + data.priceRanges[0].currency;
              this.details[4] = price_range;
            }
          }
          if(data.url !== undefined){
            this.details[6] = data.url;
          }
          if(data.seatmap !== undefined){
            if(data.seatmap.staticUrl !== undefined){
              this.details[7] = data.seatmap.staticUrl;
            }
          }
          if(this.details[6] === 'N/A'){
            this.details[6] = '#';
          }
          if(this.details[7] === 'N/A'){
            this.details[7] = '#';
          }
          if(data.classifications !== undefined){
            if(data.classifications[0] !== undefined){
              if(data.classifications[0].segment !== undefined){
                if(data.classifications[0].segment.name!== undefined){
                  if(data.classifications[0].segment.name === 'Music'){
                    this.details[8] = this.details[0];
                  }
                }
              }
            }
          }
          this.detailsChange.emit(this.details);
      });
    }catch(error){
      console.log('get event details error: ',error);
    }
  }
  onClickfavorite(i:number){
    if(this.results[i]['favorite'] === false){
      localStorage.setItem(String(i), JSON.stringify(this.results[i]));
    }else{
      localStorage.removeItem(String(i));
    }
    this.results[i]['favorite'] = !this.results[i]['favorite'];
    this.resultsChange.emit(this.results);
  }
}
