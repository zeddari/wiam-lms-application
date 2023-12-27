import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ICoteryHistory } from '../cotery-history.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../cotery-history.test-samples';

import { CoteryHistoryService, RestCoteryHistory } from './cotery-history.service';

const requireRestSample: RestCoteryHistory = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
};

describe('CoteryHistory Service', () => {
  let service: CoteryHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: ICoteryHistory | ICoteryHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CoteryHistoryService);
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

    it('should create a CoteryHistory', () => {
      const coteryHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(coteryHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CoteryHistory', () => {
      const coteryHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(coteryHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CoteryHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CoteryHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CoteryHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a CoteryHistory', () => {
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

    describe('addCoteryHistoryToCollectionIfMissing', () => {
      it('should add a CoteryHistory to an empty array', () => {
        const coteryHistory: ICoteryHistory = sampleWithRequiredData;
        expectedResult = service.addCoteryHistoryToCollectionIfMissing([], coteryHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coteryHistory);
      });

      it('should not add a CoteryHistory to an array that contains it', () => {
        const coteryHistory: ICoteryHistory = sampleWithRequiredData;
        const coteryHistoryCollection: ICoteryHistory[] = [
          {
            ...coteryHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCoteryHistoryToCollectionIfMissing(coteryHistoryCollection, coteryHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CoteryHistory to an array that doesn't contain it", () => {
        const coteryHistory: ICoteryHistory = sampleWithRequiredData;
        const coteryHistoryCollection: ICoteryHistory[] = [sampleWithPartialData];
        expectedResult = service.addCoteryHistoryToCollectionIfMissing(coteryHistoryCollection, coteryHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coteryHistory);
      });

      it('should add only unique CoteryHistory to an array', () => {
        const coteryHistoryArray: ICoteryHistory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const coteryHistoryCollection: ICoteryHistory[] = [sampleWithRequiredData];
        expectedResult = service.addCoteryHistoryToCollectionIfMissing(coteryHistoryCollection, ...coteryHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const coteryHistory: ICoteryHistory = sampleWithRequiredData;
        const coteryHistory2: ICoteryHistory = sampleWithPartialData;
        expectedResult = service.addCoteryHistoryToCollectionIfMissing([], coteryHistory, coteryHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coteryHistory);
        expect(expectedResult).toContain(coteryHistory2);
      });

      it('should accept null and undefined values', () => {
        const coteryHistory: ICoteryHistory = sampleWithRequiredData;
        expectedResult = service.addCoteryHistoryToCollectionIfMissing([], null, coteryHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coteryHistory);
      });

      it('should return initial array if no CoteryHistory is added', () => {
        const coteryHistoryCollection: ICoteryHistory[] = [sampleWithRequiredData];
        expectedResult = service.addCoteryHistoryToCollectionIfMissing(coteryHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(coteryHistoryCollection);
      });
    });

    describe('compareCoteryHistory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCoteryHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCoteryHistory(entity1, entity2);
        const compareResult2 = service.compareCoteryHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCoteryHistory(entity1, entity2);
        const compareResult2 = service.compareCoteryHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCoteryHistory(entity1, entity2);
        const compareResult2 = service.compareCoteryHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
