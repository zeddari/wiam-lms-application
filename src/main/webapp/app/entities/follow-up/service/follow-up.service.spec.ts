import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFollowUp } from '../follow-up.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../follow-up.test-samples';

import { FollowUpService } from './follow-up.service';

const requireRestSample: IFollowUp = {
  ...sampleWithRequiredData,
};

describe('FollowUp Service', () => {
  let service: FollowUpService;
  let httpMock: HttpTestingController;
  let expectedResult: IFollowUp | IFollowUp[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FollowUpService);
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

    it('should create a FollowUp', () => {
      const followUp = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(followUp).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FollowUp', () => {
      const followUp = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(followUp).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FollowUp', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FollowUp', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FollowUp', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a FollowUp', () => {
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

    describe('addFollowUpToCollectionIfMissing', () => {
      it('should add a FollowUp to an empty array', () => {
        const followUp: IFollowUp = sampleWithRequiredData;
        expectedResult = service.addFollowUpToCollectionIfMissing([], followUp);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(followUp);
      });

      it('should not add a FollowUp to an array that contains it', () => {
        const followUp: IFollowUp = sampleWithRequiredData;
        const followUpCollection: IFollowUp[] = [
          {
            ...followUp,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFollowUpToCollectionIfMissing(followUpCollection, followUp);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FollowUp to an array that doesn't contain it", () => {
        const followUp: IFollowUp = sampleWithRequiredData;
        const followUpCollection: IFollowUp[] = [sampleWithPartialData];
        expectedResult = service.addFollowUpToCollectionIfMissing(followUpCollection, followUp);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(followUp);
      });

      it('should add only unique FollowUp to an array', () => {
        const followUpArray: IFollowUp[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const followUpCollection: IFollowUp[] = [sampleWithRequiredData];
        expectedResult = service.addFollowUpToCollectionIfMissing(followUpCollection, ...followUpArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const followUp: IFollowUp = sampleWithRequiredData;
        const followUp2: IFollowUp = sampleWithPartialData;
        expectedResult = service.addFollowUpToCollectionIfMissing([], followUp, followUp2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(followUp);
        expect(expectedResult).toContain(followUp2);
      });

      it('should accept null and undefined values', () => {
        const followUp: IFollowUp = sampleWithRequiredData;
        expectedResult = service.addFollowUpToCollectionIfMissing([], null, followUp, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(followUp);
      });

      it('should return initial array if no FollowUp is added', () => {
        const followUpCollection: IFollowUp[] = [sampleWithRequiredData];
        expectedResult = service.addFollowUpToCollectionIfMissing(followUpCollection, undefined, null);
        expect(expectedResult).toEqual(followUpCollection);
      });
    });

    describe('compareFollowUp', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFollowUp(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFollowUp(entity1, entity2);
        const compareResult2 = service.compareFollowUp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFollowUp(entity1, entity2);
        const compareResult2 = service.compareFollowUp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFollowUp(entity1, entity2);
        const compareResult2 = service.compareFollowUp(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
