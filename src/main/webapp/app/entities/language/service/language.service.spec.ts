import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILanguage } from '../language.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../language.test-samples';

import { LanguageService } from './language.service';

const requireRestSample: ILanguage = {
  ...sampleWithRequiredData,
};

describe('Language Service', () => {
  let service: LanguageService;
  let httpMock: HttpTestingController;
  let expectedResult: ILanguage | ILanguage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LanguageService);
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

    it('should create a Language', () => {
      const language = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(language).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Language', () => {
      const language = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(language).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Language', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Language', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Language', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Language', () => {
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

    describe('addLanguageToCollectionIfMissing', () => {
      it('should add a Language to an empty array', () => {
        const language: ILanguage = sampleWithRequiredData;
        expectedResult = service.addLanguageToCollectionIfMissing([], language);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(language);
      });

      it('should not add a Language to an array that contains it', () => {
        const language: ILanguage = sampleWithRequiredData;
        const languageCollection: ILanguage[] = [
          {
            ...language,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLanguageToCollectionIfMissing(languageCollection, language);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Language to an array that doesn't contain it", () => {
        const language: ILanguage = sampleWithRequiredData;
        const languageCollection: ILanguage[] = [sampleWithPartialData];
        expectedResult = service.addLanguageToCollectionIfMissing(languageCollection, language);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(language);
      });

      it('should add only unique Language to an array', () => {
        const languageArray: ILanguage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const languageCollection: ILanguage[] = [sampleWithRequiredData];
        expectedResult = service.addLanguageToCollectionIfMissing(languageCollection, ...languageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const language: ILanguage = sampleWithRequiredData;
        const language2: ILanguage = sampleWithPartialData;
        expectedResult = service.addLanguageToCollectionIfMissing([], language, language2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(language);
        expect(expectedResult).toContain(language2);
      });

      it('should accept null and undefined values', () => {
        const language: ILanguage = sampleWithRequiredData;
        expectedResult = service.addLanguageToCollectionIfMissing([], null, language, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(language);
      });

      it('should return initial array if no Language is added', () => {
        const languageCollection: ILanguage[] = [sampleWithRequiredData];
        expectedResult = service.addLanguageToCollectionIfMissing(languageCollection, undefined, null);
        expect(expectedResult).toEqual(languageCollection);
      });
    });

    describe('compareLanguage', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLanguage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareLanguage(entity1, entity2);
        const compareResult2 = service.compareLanguage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareLanguage(entity1, entity2);
        const compareResult2 = service.compareLanguage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareLanguage(entity1, entity2);
        const compareResult2 = service.compareLanguage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
