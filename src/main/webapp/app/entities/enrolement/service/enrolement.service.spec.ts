import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEnrolement } from '../enrolement.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../enrolement.test-samples';

import { EnrolementService, RestEnrolement } from './enrolement.service';

const requireRestSample: RestEnrolement = {
  ...sampleWithRequiredData,
  activatedAt: sampleWithRequiredData.activatedAt?.toJSON(),
  activatedBy: sampleWithRequiredData.activatedBy?.toJSON(),
  enrolmentStartTime: sampleWithRequiredData.enrolmentStartTime?.toJSON(),
  enrolemntEndTime: sampleWithRequiredData.enrolemntEndTime?.toJSON(),
};

describe('Enrolement Service', () => {
  let service: EnrolementService;
  let httpMock: HttpTestingController;
  let expectedResult: IEnrolement | IEnrolement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnrolementService);
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

    it('should create a Enrolement', () => {
      const enrolement = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(enrolement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Enrolement', () => {
      const enrolement = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(enrolement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Enrolement', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Enrolement', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Enrolement', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Enrolement', () => {
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

    describe('addEnrolementToCollectionIfMissing', () => {
      it('should add a Enrolement to an empty array', () => {
        const enrolement: IEnrolement = sampleWithRequiredData;
        expectedResult = service.addEnrolementToCollectionIfMissing([], enrolement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(enrolement);
      });

      it('should not add a Enrolement to an array that contains it', () => {
        const enrolement: IEnrolement = sampleWithRequiredData;
        const enrolementCollection: IEnrolement[] = [
          {
            ...enrolement,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEnrolementToCollectionIfMissing(enrolementCollection, enrolement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Enrolement to an array that doesn't contain it", () => {
        const enrolement: IEnrolement = sampleWithRequiredData;
        const enrolementCollection: IEnrolement[] = [sampleWithPartialData];
        expectedResult = service.addEnrolementToCollectionIfMissing(enrolementCollection, enrolement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(enrolement);
      });

      it('should add only unique Enrolement to an array', () => {
        const enrolementArray: IEnrolement[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const enrolementCollection: IEnrolement[] = [sampleWithRequiredData];
        expectedResult = service.addEnrolementToCollectionIfMissing(enrolementCollection, ...enrolementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const enrolement: IEnrolement = sampleWithRequiredData;
        const enrolement2: IEnrolement = sampleWithPartialData;
        expectedResult = service.addEnrolementToCollectionIfMissing([], enrolement, enrolement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(enrolement);
        expect(expectedResult).toContain(enrolement2);
      });

      it('should accept null and undefined values', () => {
        const enrolement: IEnrolement = sampleWithRequiredData;
        expectedResult = service.addEnrolementToCollectionIfMissing([], null, enrolement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(enrolement);
      });

      it('should return initial array if no Enrolement is added', () => {
        const enrolementCollection: IEnrolement[] = [sampleWithRequiredData];
        expectedResult = service.addEnrolementToCollectionIfMissing(enrolementCollection, undefined, null);
        expect(expectedResult).toEqual(enrolementCollection);
      });
    });

    describe('compareEnrolement', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEnrolement(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEnrolement(entity1, entity2);
        const compareResult2 = service.compareEnrolement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEnrolement(entity1, entity2);
        const compareResult2 = service.compareEnrolement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEnrolement(entity1, entity2);
        const compareResult2 = service.compareEnrolement(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
