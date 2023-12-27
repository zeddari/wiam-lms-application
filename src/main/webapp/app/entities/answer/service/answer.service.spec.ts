import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAnswer } from '../answer.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../answer.test-samples';

import { AnswerService } from './answer.service';

const requireRestSample: IAnswer = {
  ...sampleWithRequiredData,
};

describe('Answer Service', () => {
  let service: AnswerService;
  let httpMock: HttpTestingController;
  let expectedResult: IAnswer | IAnswer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AnswerService);
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

    it('should create a Answer', () => {
      const answer = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(answer).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Answer', () => {
      const answer = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(answer).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Answer', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Answer', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Answer', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Answer', () => {
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

    describe('addAnswerToCollectionIfMissing', () => {
      it('should add a Answer to an empty array', () => {
        const answer: IAnswer = sampleWithRequiredData;
        expectedResult = service.addAnswerToCollectionIfMissing([], answer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(answer);
      });

      it('should not add a Answer to an array that contains it', () => {
        const answer: IAnswer = sampleWithRequiredData;
        const answerCollection: IAnswer[] = [
          {
            ...answer,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, answer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Answer to an array that doesn't contain it", () => {
        const answer: IAnswer = sampleWithRequiredData;
        const answerCollection: IAnswer[] = [sampleWithPartialData];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, answer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(answer);
      });

      it('should add only unique Answer to an array', () => {
        const answerArray: IAnswer[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const answerCollection: IAnswer[] = [sampleWithRequiredData];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, ...answerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const answer: IAnswer = sampleWithRequiredData;
        const answer2: IAnswer = sampleWithPartialData;
        expectedResult = service.addAnswerToCollectionIfMissing([], answer, answer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(answer);
        expect(expectedResult).toContain(answer2);
      });

      it('should accept null and undefined values', () => {
        const answer: IAnswer = sampleWithRequiredData;
        expectedResult = service.addAnswerToCollectionIfMissing([], null, answer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(answer);
      });

      it('should return initial array if no Answer is added', () => {
        const answerCollection: IAnswer[] = [sampleWithRequiredData];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, undefined, null);
        expect(expectedResult).toEqual(answerCollection);
      });
    });

    describe('compareAnswer', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAnswer(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAnswer(entity1, entity2);
        const compareResult2 = service.compareAnswer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAnswer(entity1, entity2);
        const compareResult2 = service.compareAnswer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAnswer(entity1, entity2);
        const compareResult2 = service.compareAnswer(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
