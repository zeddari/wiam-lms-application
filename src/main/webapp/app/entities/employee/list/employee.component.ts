import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { SortService } from 'app/shared/sort/sort.service';
import { IEmployee } from '../employee.model';
import { EntityArrayResponseType, EmployeeService } from '../service/employee.service';
import { EmployeeDeleteDialogComponent } from '../delete/employee-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-employee',
  templateUrl: './employee.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class EmployeeComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['phoneNumber', 'mobileNumber', 'about', 'imageLink', 'code', 'lastDegree'];

  employees?: IEmployee[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  currentSearch = '';

  constructor(
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
  ) {}

  trackId = (_index: number, item: IEmployee): number => this.employeeService.getEmployeeIdentifier(item);

  search(query: string): void {
    if (query && EmployeeComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
      this.predicate = 'id';
      this.ascending = true;
    }
    this.currentSearch = query;
    this.navigateToWithComponentValues();
  }

  ngOnInit(): void {
    this.load();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(employee: IEmployee): void {
    const modalRef = this.modalService.open(EmployeeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.employee = employee;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations()),
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.predicate, this.ascending, this.currentSearch);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.predicate, this.ascending, this.currentSearch)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      if (EmployeeComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
        this.predicate = '';
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.employees = this.refineData(dataFromBody);
  }

  protected refineData(data: IEmployee[]): IEmployee[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: IEmployee[] | null): IEmployee[] {
    return data ?? [];
  }

  protected queryBackend(predicate?: string, ascending?: boolean, currentSearch?: string): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      query: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    if (this.currentSearch && this.currentSearch !== '') {
      return this.employeeService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    } else {
      return this.employeeService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
  }

  protected handleNavigation(predicate?: string, ascending?: boolean, currentSearch?: string): void {
    const queryParamsObj = {
      search: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
