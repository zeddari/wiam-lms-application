import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISessionJoinMode } from '../session-join-mode.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../session-join-mode.test-samples';

import { SessionJoinModeService } from './session-join-mode.service';

const requireRestSample: ISessionJoinMode = {
  ...sampleWithRequiredData,
};

describe('SessionJoinMode Service', () => {
  let service: SessionJoinModeService;
  let httpMock: HttpTestingController;
  let expectedResult: ISessionJoinMode | ISessionJoinMode[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SessionJoinModeService);
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

    it('should create a SessionJoinMode', () => {
      const sessionJoinMode = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sessionJoinMode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SessionJoinMode', () => {
      const sessionJoinMode = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sessionJoinMode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SessionJoinMode', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SessionJoinMode', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SessionJoinMode', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a SessionJoinMode', () => {
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

    describe('addSessionJoinModeToCollectionIfMissing', () => {
      it('should add a SessionJoinMode to an empty array', () => {
        const sessionJoinMode: ISessionJoinMode = sampleWithRequiredData;
        expectedResult = service.addSessionJoinModeToCollectionIfMissing([], sessionJoinMode);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionJoinMode);
      });

      it('should not add a SessionJoinMode to an array that contains it', () => {
        const sessionJoinMode: ISessionJoinMode = sampleWithRequiredData;
        const sessionJoinModeCollection: ISessionJoinMode[] = [
          {
            ...sessionJoinMode,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSessionJoinModeToCollectionIfMissing(sessionJoinModeCollection, sessionJoinMode);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SessionJoinMode to an array that doesn't contain it", () => {
        const sessionJoinMode: ISessionJoinMode = sampleWithRequiredData;
        const sessionJoinModeCollection: ISessionJoinMode[] = [sampleWithPartialData];
        expectedResult = service.addSessionJoinModeToCollectionIfMissing(sessionJoinModeCollection, sessionJoinMode);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionJoinMode);
      });

      it('should add only unique SessionJoinMode to an array', () => {
        const sessionJoinModeArray: ISessionJoinMode[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sessionJoinModeCollection: ISessionJoinMode[] = [sampleWithRequiredData];
        expectedResult = service.addSessionJoinModeToCollectionIfMissing(sessionJoinModeCollection, ...sessionJoinModeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sessionJoinMode: ISessionJoinMode = sampleWithRequiredData;
        const sessionJoinMode2: ISessionJoinMode = sampleWithPartialData;
        expectedResult = service.addSessionJoinModeToCollectionIfMissing([], sessionJoinMode, sessionJoinMode2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionJoinMode);
        expect(expectedResult).toContain(sessionJoinMode2);
      });

      it('should accept null and undefined values', () => {
        const sessionJoinMode: ISessionJoinMode = sampleWithRequiredData;
        expectedResult = service.addSessionJoinModeToCollectionIfMissing([], null, sessionJoinMode, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionJoinMode);
      });

      it('should return initial array if no SessionJoinMode is added', () => {
        const sessionJoinModeCollection: ISessionJoinMode[] = [sampleWithRequiredData];
        expectedResult = service.addSessionJoinModeToCollectionIfMissing(sessionJoinModeCollection, undefined, null);
        expect(expectedResult).toEqual(sessionJoinModeCollection);
      });
    });

    describe('compareSessionJoinMode', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSessionJoinMode(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSessionJoinMode(entity1, entity2);
        const compareResult2 = service.compareSessionJoinMode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSessionJoinMode(entity1, entity2);
        const compareResult2 = service.compareSessionJoinMode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSessionJoinMode(entity1, entity2);
        const compareResult2 = service.compareSessionJoinMode(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
