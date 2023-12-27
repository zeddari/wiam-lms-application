import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IQuestion2 } from '../question-2.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../question-2.test-samples';

import { Question2Service } from './question-2.service';

const requireRestSample: IQuestion2 = {
  ...sampleWithRequiredData,
};

describe('Question2 Service', () => {
  let service: Question2Service;
  let httpMock: HttpTestingController;
  let expectedResult: IQuestion2 | IQuestion2[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(Question2Service);
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

    it('should create a Question2', () => {
      const question2 = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(question2).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Question2', () => {
      const question2 = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(question2).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Question2', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Question2', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Question2', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Question2', () => {
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

    describe('addQuestion2ToCollectionIfMissing', () => {
      it('should add a Question2 to an empty array', () => {
        const question2: IQuestion2 = sampleWithRequiredData;
        expectedResult = service.addQuestion2ToCollectionIfMissing([], question2);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(question2);
      });

      it('should not add a Question2 to an array that contains it', () => {
        const question2: IQuestion2 = sampleWithRequiredData;
        const question2Collection: IQuestion2[] = [
          {
            ...question2,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addQuestion2ToCollectionIfMissing(question2Collection, question2);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Question2 to an array that doesn't contain it", () => {
        const question2: IQuestion2 = sampleWithRequiredData;
        const question2Collection: IQuestion2[] = [sampleWithPartialData];
        expectedResult = service.addQuestion2ToCollectionIfMissing(question2Collection, question2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(question2);
      });

      it('should add only unique Question2 to an array', () => {
        const question2Array: IQuestion2[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const question2Collection: IQuestion2[] = [sampleWithRequiredData];
        expectedResult = service.addQuestion2ToCollectionIfMissing(question2Collection, ...question2Array);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const question2: IQuestion2 = sampleWithRequiredData;
        const question22: IQuestion2 = sampleWithPartialData;
        expectedResult = service.addQuestion2ToCollectionIfMissing([], question2, question22);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(question2);
        expect(expectedResult).toContain(question22);
      });

      it('should accept null and undefined values', () => {
        const question2: IQuestion2 = sampleWithRequiredData;
        expectedResult = service.addQuestion2ToCollectionIfMissing([], null, question2, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(question2);
      });

      it('should return initial array if no Question2 is added', () => {
        const question2Collection: IQuestion2[] = [sampleWithRequiredData];
        expectedResult = service.addQuestion2ToCollectionIfMissing(question2Collection, undefined, null);
        expect(expectedResult).toEqual(question2Collection);
      });
    });

    describe('compareQuestion2', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareQuestion2(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareQuestion2(entity1, entity2);
        const compareResult2 = service.compareQuestion2(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareQuestion2(entity1, entity2);
        const compareResult2 = service.compareQuestion2(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareQuestion2(entity1, entity2);
        const compareResult2 = service.compareQuestion2(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
