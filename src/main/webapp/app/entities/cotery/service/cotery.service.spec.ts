import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ICotery } from '../cotery.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../cotery.test-samples';

import { CoteryService, RestCotery } from './cotery.service';

const requireRestSample: RestCotery = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
};

describe('Cotery Service', () => {
  let service: CoteryService;
  let httpMock: HttpTestingController;
  let expectedResult: ICotery | ICotery[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CoteryService);
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

    it('should create a Cotery', () => {
      const cotery = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cotery).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cotery', () => {
      const cotery = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cotery).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cotery', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cotery', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Cotery', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Cotery', () => {
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

    describe('addCoteryToCollectionIfMissing', () => {
      it('should add a Cotery to an empty array', () => {
        const cotery: ICotery = sampleWithRequiredData;
        expectedResult = service.addCoteryToCollectionIfMissing([], cotery);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cotery);
      });

      it('should not add a Cotery to an array that contains it', () => {
        const cotery: ICotery = sampleWithRequiredData;
        const coteryCollection: ICotery[] = [
          {
            ...cotery,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCoteryToCollectionIfMissing(coteryCollection, cotery);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cotery to an array that doesn't contain it", () => {
        const cotery: ICotery = sampleWithRequiredData;
        const coteryCollection: ICotery[] = [sampleWithPartialData];
        expectedResult = service.addCoteryToCollectionIfMissing(coteryCollection, cotery);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cotery);
      });

      it('should add only unique Cotery to an array', () => {
        const coteryArray: ICotery[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const coteryCollection: ICotery[] = [sampleWithRequiredData];
        expectedResult = service.addCoteryToCollectionIfMissing(coteryCollection, ...coteryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cotery: ICotery = sampleWithRequiredData;
        const cotery2: ICotery = sampleWithPartialData;
        expectedResult = service.addCoteryToCollectionIfMissing([], cotery, cotery2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cotery);
        expect(expectedResult).toContain(cotery2);
      });

      it('should accept null and undefined values', () => {
        const cotery: ICotery = sampleWithRequiredData;
        expectedResult = service.addCoteryToCollectionIfMissing([], null, cotery, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cotery);
      });

      it('should return initial array if no Cotery is added', () => {
        const coteryCollection: ICotery[] = [sampleWithRequiredData];
        expectedResult = service.addCoteryToCollectionIfMissing(coteryCollection, undefined, null);
        expect(expectedResult).toEqual(coteryCollection);
      });
    });

    describe('compareCotery', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCotery(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCotery(entity1, entity2);
        const compareResult2 = service.compareCotery(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCotery(entity1, entity2);
        const compareResult2 = service.compareCotery(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCotery(entity1, entity2);
        const compareResult2 = service.compareCotery(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
