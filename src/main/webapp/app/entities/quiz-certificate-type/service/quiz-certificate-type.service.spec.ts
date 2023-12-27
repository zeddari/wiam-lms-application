import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IQuizCertificateType } from '../quiz-certificate-type.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../quiz-certificate-type.test-samples';

import { QuizCertificateTypeService } from './quiz-certificate-type.service';

const requireRestSample: IQuizCertificateType = {
  ...sampleWithRequiredData,
};

describe('QuizCertificateType Service', () => {
  let service: QuizCertificateTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IQuizCertificateType | IQuizCertificateType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(QuizCertificateTypeService);
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

    it('should create a QuizCertificateType', () => {
      const quizCertificateType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(quizCertificateType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a QuizCertificateType', () => {
      const quizCertificateType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(quizCertificateType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a QuizCertificateType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of QuizCertificateType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a QuizCertificateType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a QuizCertificateType', () => {
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

    describe('addQuizCertificateTypeToCollectionIfMissing', () => {
      it('should add a QuizCertificateType to an empty array', () => {
        const quizCertificateType: IQuizCertificateType = sampleWithRequiredData;
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing([], quizCertificateType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quizCertificateType);
      });

      it('should not add a QuizCertificateType to an array that contains it', () => {
        const quizCertificateType: IQuizCertificateType = sampleWithRequiredData;
        const quizCertificateTypeCollection: IQuizCertificateType[] = [
          {
            ...quizCertificateType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing(quizCertificateTypeCollection, quizCertificateType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a QuizCertificateType to an array that doesn't contain it", () => {
        const quizCertificateType: IQuizCertificateType = sampleWithRequiredData;
        const quizCertificateTypeCollection: IQuizCertificateType[] = [sampleWithPartialData];
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing(quizCertificateTypeCollection, quizCertificateType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quizCertificateType);
      });

      it('should add only unique QuizCertificateType to an array', () => {
        const quizCertificateTypeArray: IQuizCertificateType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const quizCertificateTypeCollection: IQuizCertificateType[] = [sampleWithRequiredData];
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing(quizCertificateTypeCollection, ...quizCertificateTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const quizCertificateType: IQuizCertificateType = sampleWithRequiredData;
        const quizCertificateType2: IQuizCertificateType = sampleWithPartialData;
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing([], quizCertificateType, quizCertificateType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quizCertificateType);
        expect(expectedResult).toContain(quizCertificateType2);
      });

      it('should accept null and undefined values', () => {
        const quizCertificateType: IQuizCertificateType = sampleWithRequiredData;
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing([], null, quizCertificateType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quizCertificateType);
      });

      it('should return initial array if no QuizCertificateType is added', () => {
        const quizCertificateTypeCollection: IQuizCertificateType[] = [sampleWithRequiredData];
        expectedResult = service.addQuizCertificateTypeToCollectionIfMissing(quizCertificateTypeCollection, undefined, null);
        expect(expectedResult).toEqual(quizCertificateTypeCollection);
      });
    });

    describe('compareQuizCertificateType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareQuizCertificateType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareQuizCertificateType(entity1, entity2);
        const compareResult2 = service.compareQuizCertificateType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareQuizCertificateType(entity1, entity2);
        const compareResult2 = service.compareQuizCertificateType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareQuizCertificateType(entity1, entity2);
        const compareResult2 = service.compareQuizCertificateType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
