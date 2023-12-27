import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISessionMode } from '../session-mode.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../session-mode.test-samples';

import { SessionModeService } from './session-mode.service';

const requireRestSample: ISessionMode = {
  ...sampleWithRequiredData,
};

describe('SessionMode Service', () => {
  let service: SessionModeService;
  let httpMock: HttpTestingController;
  let expectedResult: ISessionMode | ISessionMode[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SessionModeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SessionMode', () => {
      const sessionMode = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sessionMode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SessionMode', () => {
      const sessionMode = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sessionMode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SessionMode', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SessionMode', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SessionMode', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a SessionMode', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addSessionModeToCollectionIfMissing', () => {
      it('should add a SessionMode to an empty array', () => {
        const sessionMode: ISessionMode = sampleWithRequiredData;
        expectedResult = service.addSessionModeToCollectionIfMissing([], sessionMode);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionMode);
      });

      it('should not add a SessionMode to an array that contains it', () => {
        const sessionMode: ISessionMode = sampleWithRequiredData;
        const sessionModeCollection: ISessionMode[] = [
          {
            ...sessionMode,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSessionModeToCollectionIfMissing(sessionModeCollection, sessionMode);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SessionMode to an array that doesn't contain it", () => {
        const sessionMode: ISessionMode = sampleWithRequiredData;
        const sessionModeCollection: ISessionMode[] = [sampleWithPartialData];
        expectedResult = service.addSessionModeToCollectionIfMissing(sessionModeCollection, sessionMode);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionMode);
      });

      it('should add only unique SessionMode to an array', () => {
        const sessionModeArray: ISessionMode[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sessionModeCollection: ISessionMode[] = [sampleWithRequiredData];
        expectedResult = service.addSessionModeToCollectionIfMissing(sessionModeCollection, ...sessionModeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sessionMode: ISessionMode = sampleWithRequiredData;
        const sessionMode2: ISessionMode = sampleWithPartialData;
        expectedResult = service.addSessionModeToCollectionIfMissing([], sessionMode, sessionMode2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionMode);
        expect(expectedResult).toContain(sessionMode2);
      });

      it('should accept null and undefined values', () => {
        const sessionMode: ISessionMode = sampleWithRequiredData;
        expectedResult = service.addSessionModeToCollectionIfMissing([], null, sessionMode, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionMode);
      });

      it('should return initial array if no SessionMode is added', () => {
        const sessionModeCollection: ISessionMode[] = [sampleWithRequiredData];
        expectedResult = service.addSessionModeToCollectionIfMissing(sessionModeCollection, undefined, null);
        expect(expectedResult).toEqual(sessionModeCollection);
      });
    });

    describe('compareSessionMode', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSessionMode(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSessionMode(entity1, entity2);
        const compareResult2 = service.compareSessionMode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSessionMode(entity1, entity2);
        const compareResult2 = service.compareSessionMode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSessionMode(entity1, entity2);
        const compareResult2 = service.compareSessionMode(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
