import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";


@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {
  @Input() results:any[]=[];
  @Output() resultsChange = new EventEmitter<any[]>();
  @Input() details:any[]=[];
  @Output() detailsChange = new EventEmitter<any[]>();
  @Input() lastDetailIndex:number=0;
  //venueDetails:any[]=['Address', 'City', 'Phone Number', 'Open Hours', 'General Rule', 'Child Rule'];
  artistDetails:any[]=['N/A', 'N/A', 'N/A', 'N/A'];
  venueDetails:any[]=['N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A'];
  lat = 22.2736308;
  long = 70.7512555;
  zoom = 15;
  constructor(private http: HttpClient) { }
  ngOnInit(): void {
  }

  showVenueDetails(){
    if(this.details[9] !== undefined && this.details[9] !== 'N/A'){
      let params = new HttpParams();
      params = params.append('id', this.details[9]);
      try{
        this.http.get('https://hw8-eventsearch-nodejs.uw.r.appspot.com/venuedetails', {params: params}).subscribe(res=>{
          console.log("venue details");
          console.log(res);
          interface ExampleObject {
            [key: string]: any
          }
          let data: ExampleObject = res;
          if(data.address !== undefined){
            if(data.address.line1 !== undefined){
              this.venueDetails[0] = data.address.line1;
            }
          }
          if(data.location !== undefined){
            if(data.location.latitude !== undefined){
              this.lat = Number(data.location.latitude);
            }
            if(data.location.longitude !== undefined){
              this.long = Number(data.location.longitude);
            }
          }
          if(data.city !== undefined){
            if(data.city.name !== undefined){
              this.venueDetails[1] = data.city.name;
            }
          }
          if(data.boxOfficeInfo !== undefined){
            if(data.boxOfficeInfo.phoneNumberDetail !== undefined){
              this.venueDetails[2] = data.boxOfficeInfo.phoneNumberDetail;
            }
            if(data.boxOfficeInfo.openHoursDetail !== undefined){
              this.venueDetails[3] = data.boxOfficeInfo.openHoursDetail;
            }
          }
          if(data.generalInfo !== undefined){
            if(data.generalInfo.generalRule !== undefined){
              this.venueDetails[4] = data.generalInfo.generalRule;
            }
            if(data.generalInfo.childRule !== undefined){
              this.venueDetails[5] = data.generalInfo.childRule;
            }
          }
        });
      }catch(err){
        console.log(err);
      }
    }
  }

  onClickfavorite(){
    if(this.results[this.lastDetailIndex]['favorite'] === false){
      localStorage.setItem(String(this.lastDetailIndex), JSON.stringify(this.results[this.lastDetailIndex]));
    }else{
      localStorage.removeItem(String(this.lastDetailIndex));
    }
    this.details[11] = !this.details[11];
    this.detailsChange.emit(this.details);
    this.results[this.lastDetailIndex]['favorite'] = !this.results[this.lastDetailIndex]['favorite'];
    this.resultsChange.emit(this.results);
  }

  getArtistDetails(keyword:string){
    if(keyword !== undefined && keyword !== ''){
      try{
        let params = new HttpParams();
        params = params.append('keyword', keyword);
        this.http.get('https://hw8-eventsearch-nodejs.uw.r.appspot.com/spotify', {params: params}).subscribe(res=>{
          interface ExampleObject {
            [key: string]: any
          }
          let data: ExampleObject = res;
          if(data.artists !== undefined){
            if(data.artists.items !== undefined){
              let item = data.artists.items[0];
              if(item.name !== undefined){
                this.artistDetails[0] = item.name;
              }
              if(item.followers !== undefined){
                if(item.followers.total !== undefined){
                  this.artistDetails[1] = item.followers.total;
                }
              }
              if(item.popularity !== undefined){
                this.artistDetails[2] = item.popularity;
              }
              if(item.external_urls !== undefined){
                if(item.external_urls.spotify !== undefined){
                  this.artistDetails[3] = item.external_urls.spotify;
                }
              }
            }
          }
        });
      }catch(err){
        console.log(err);
      }
    }
  }
}
