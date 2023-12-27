import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IQuizCertificate } from '../quiz-certificate.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../quiz-certificate.test-samples';

import { QuizCertificateService } from './quiz-certificate.service';

const requireRestSample: IQuizCertificate = {
  ...sampleWithRequiredData,
};

describe('QuizCertificate Service', () => {
  let service: QuizCertificateService;
  let httpMock: HttpTestingController;
  let expectedResult: IQuizCertificate | IQuizCertificate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(QuizCertificateService);
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

    it('should create a QuizCertificate', () => {
      const quizCertificate = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(quizCertificate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a QuizCertificate', () => {
      const quizCertificate = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(quizCertificate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a QuizCertificate', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of QuizCertificate', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a QuizCertificate', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a QuizCertificate', () => {
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

    describe('addQuizCertificateToCollectionIfMissing', () => {
      it('should add a QuizCertificate to an empty array', () => {
        const quizCertificate: IQuizCertificate = sampleWithRequiredData;
        expectedResult = service.addQuizCertificateToCollectionIfMissing([], quizCertificate);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quizCertificate);
      });

      it('should not add a QuizCertificate to an array that contains it', () => {
        const quizCertificate: IQuizCertificate = sampleWithRequiredData;
        const quizCertificateCollection: IQuizCertificate[] = [
          {
            ...quizCertificate,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addQuizCertificateToCollectionIfMissing(quizCertificateCollection, quizCertificate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a QuizCertificate to an array that doesn't contain it", () => {
        const quizCertificate: IQuizCertificate = sampleWithRequiredData;
        const quizCertificateCollection: IQuizCertificate[] = [sampleWithPartialData];
        expectedResult = service.addQuizCertificateToCollectionIfMissing(quizCertificateCollection, quizCertificate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quizCertificate);
      });

      it('should add only unique QuizCertificate to an array', () => {
        const quizCertificateArray: IQuizCertificate[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const quizCertificateCollection: IQuizCertificate[] = [sampleWithRequiredData];
        expectedResult = service.addQuizCertificateToCollectionIfMissing(quizCertificateCollection, ...quizCertificateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const quizCertificate: IQuizCertificate = sampleWithRequiredData;
        const quizCertificate2: IQuizCertificate = sampleWithPartialData;
        expectedResult = service.addQuizCertificateToCollectionIfMissing([], quizCertificate, quizCertificate2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quizCertificate);
        expect(expectedResult).toContain(quizCertificate2);
      });

      it('should accept null and undefined values', () => {
        const quizCertificate: IQuizCertificate = sampleWithRequiredData;
        expectedResult = service.addQuizCertificateToCollectionIfMissing([], null, quizCertificate, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quizCertificate);
      });

      it('should return initial array if no QuizCertificate is added', () => {
        const quizCertificateCollection: IQuizCertificate[] = [sampleWithRequiredData];
        expectedResult = service.addQuizCertificateToCollectionIfMissing(quizCertificateCollection, undefined, null);
        expect(expectedResult).toEqual(quizCertificateCollection);
      });
    });

    describe('compareQuizCertificate', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareQuizCertificate(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareQuizCertificate(entity1, entity2);
        const compareResult2 = service.compareQuizCertificate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareQuizCertificate(entity1, entity2);
        const compareResult2 = service.compareQuizCertificate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareQuizCertificate(entity1, entity2);
        const compareResult2 = service.compareQuizCertificate(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
