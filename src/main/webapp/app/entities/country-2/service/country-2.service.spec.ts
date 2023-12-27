import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICountry2 } from '../country-2.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../country-2.test-samples';

import { Country2Service } from './country-2.service';

const requireRestSample: ICountry2 = {
  ...sampleWithRequiredData,
};

describe('Country2 Service', () => {
  let service: Country2Service;
  let httpMock: HttpTestingController;
  let expectedResult: ICountry2 | ICountry2[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(Country2Service);
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

    it('should create a Country2', () => {
      const country2 = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(country2).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Country2', () => {
      const country2 = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(country2).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Country2', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Country2', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Country2', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Country2', () => {
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

    describe('addCountry2ToCollectionIfMissing', () => {
      it('should add a Country2 to an empty array', () => {
        const country2: ICountry2 = sampleWithRequiredData;
        expectedResult = service.addCountry2ToCollectionIfMissing([], country2);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(country2);
      });

      it('should not add a Country2 to an array that contains it', () => {
        const country2: ICountry2 = sampleWithRequiredData;
        const country2Collection: ICountry2[] = [
          {
            ...country2,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCountry2ToCollectionIfMissing(country2Collection, country2);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Country2 to an array that doesn't contain it", () => {
        const country2: ICountry2 = sampleWithRequiredData;
        const country2Collection: ICountry2[] = [sampleWithPartialData];
        expectedResult = service.addCountry2ToCollectionIfMissing(country2Collection, country2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(country2);
      });

      it('should add only unique Country2 to an array', () => {
        const country2Array: ICountry2[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const country2Collection: ICountry2[] = [sampleWithRequiredData];
        expectedResult = service.addCountry2ToCollectionIfMissing(country2Collection, ...country2Array);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const country2: ICountry2 = sampleWithRequiredData;
        const country22: ICountry2 = sampleWithPartialData;
        expectedResult = service.addCountry2ToCollectionIfMissing([], country2, country22);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(country2);
        expect(expectedResult).toContain(country22);
      });

      it('should accept null and undefined values', () => {
        const country2: ICountry2 = sampleWithRequiredData;
        expectedResult = service.addCountry2ToCollectionIfMissing([], null, country2, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(country2);
      });

      it('should return initial array if no Country2 is added', () => {
        const country2Collection: ICountry2[] = [sampleWithRequiredData];
        expectedResult = service.addCountry2ToCollectionIfMissing(country2Collection, undefined, null);
        expect(expectedResult).toEqual(country2Collection);
      });
    });

    describe('compareCountry2', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCountry2(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCountry2(entity1, entity2);
        const compareResult2 = service.compareCountry2(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCountry2(entity1, entity2);
        const compareResult2 = service.compareCountry2(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCountry2(entity1, entity2);
        const compareResult2 = service.compareCountry2(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
