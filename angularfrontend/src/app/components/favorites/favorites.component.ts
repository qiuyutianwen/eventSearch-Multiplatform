import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent implements OnInit {
  @Input() results:any[]=[];
  @Output() resultsChange = new EventEmitter<any[]>();
  @Input() favoriteList:any[]=[];
  @Output() favoriteListChange = new EventEmitter<any[]>();


  constructor() { }

  ngOnInit(): void {
  }

  removefavorite(index:string, i:number){
    localStorage.removeItem(index);
    this.favoriteList.splice(i, 1);
    this.favoriteListChange.emit(this.favoriteList);
    this.results[Number(index)]['favorite'] = false;
    this.resultsChange.emit(this.results);
  }
}
