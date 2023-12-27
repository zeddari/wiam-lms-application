import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserCustomService } from '../service/user-custom.service';

import { UserCustomComponent } from './user-custom.component';

describe('UserCustom Management Component', () => {
  let comp: UserCustomComponent;
  let fixture: ComponentFixture<UserCustomComponent>;
  let service: UserCustomService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'user-custom', component: UserCustomComponent }]),
        HttpClientTestingModule,
        UserCustomComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(UserCustomComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserCustomComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserCustomService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.userCustoms?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to userCustomService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUserCustomIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUserCustomIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
