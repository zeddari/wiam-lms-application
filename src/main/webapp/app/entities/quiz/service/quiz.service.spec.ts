import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IQuiz } from '../quiz.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../quiz.test-samples';

import { QuizService } from './quiz.service';

const requireRestSample: IQuiz = {
  ...sampleWithRequiredData,
};

describe('Quiz Service', () => {
  let service: QuizService;
  let httpMock: HttpTestingController;
  let expectedResult: IQuiz | IQuiz[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(QuizService);
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

    it('should create a Quiz', () => {
      const quiz = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(quiz).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Quiz', () => {
      const quiz = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(quiz).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Quiz', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Quiz', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Quiz', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Quiz', () => {
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

    describe('addQuizToCollectionIfMissing', () => {
      it('should add a Quiz to an empty array', () => {
        const quiz: IQuiz = sampleWithRequiredData;
        expectedResult = service.addQuizToCollectionIfMissing([], quiz);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quiz);
      });

      it('should not add a Quiz to an array that contains it', () => {
        const quiz: IQuiz = sampleWithRequiredData;
        const quizCollection: IQuiz[] = [
          {
            ...quiz,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addQuizToCollectionIfMissing(quizCollection, quiz);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Quiz to an array that doesn't contain it", () => {
        const quiz: IQuiz = sampleWithRequiredData;
        const quizCollection: IQuiz[] = [sampleWithPartialData];
        expectedResult = service.addQuizToCollectionIfMissing(quizCollection, quiz);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quiz);
      });

      it('should add only unique Quiz to an array', () => {
        const quizArray: IQuiz[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const quizCollection: IQuiz[] = [sampleWithRequiredData];
        expectedResult = service.addQuizToCollectionIfMissing(quizCollection, ...quizArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const quiz: IQuiz = sampleWithRequiredData;
        const quiz2: IQuiz = sampleWithPartialData;
        expectedResult = service.addQuizToCollectionIfMissing([], quiz, quiz2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quiz);
        expect(expectedResult).toContain(quiz2);
      });

      it('should accept null and undefined values', () => {
        const quiz: IQuiz = sampleWithRequiredData;
        expectedResult = service.addQuizToCollectionIfMissing([], null, quiz, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quiz);
      });

      it('should return initial array if no Quiz is added', () => {
        const quizCollection: IQuiz[] = [sampleWithRequiredData];
        expectedResult = service.addQuizToCollectionIfMissing(quizCollection, undefined, null);
        expect(expectedResult).toEqual(quizCollection);
      });
    });

    describe('compareQuiz', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareQuiz(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareQuiz(entity1, entity2);
        const compareResult2 = service.compareQuiz(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareQuiz(entity1, entity2);
        const compareResult2 = service.compareQuiz(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareQuiz(entity1, entity2);
        const compareResult2 = service.compareQuiz(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
