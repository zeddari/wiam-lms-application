import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IProgression } from '../progression.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../progression.test-samples';

import { ProgressionService, RestProgression } from './progression.service';

const requireRestSample: RestProgression = {
  ...sampleWithRequiredData,
  progressionDate: sampleWithRequiredData.progressionDate?.format(DATE_FORMAT),
};

describe('Progression Service', () => {
  let service: ProgressionService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgression | IProgression[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgressionService);
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

    it('should create a Progression', () => {
      const progression = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(progression).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Progression', () => {
      const progression = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(progression).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Progression', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Progression', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Progression', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Progression', () => {
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

    describe('addProgressionToCollectionIfMissing', () => {
      it('should add a Progression to an empty array', () => {
        const progression: IProgression = sampleWithRequiredData;
        expectedResult = service.addProgressionToCollectionIfMissing([], progression);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(progression);
      });

      it('should not add a Progression to an array that contains it', () => {
        const progression: IProgression = sampleWithRequiredData;
        const progressionCollection: IProgression[] = [
          {
            ...progression,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgressionToCollectionIfMissing(progressionCollection, progression);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Progression to an array that doesn't contain it", () => {
        const progression: IProgression = sampleWithRequiredData;
        const progressionCollection: IProgression[] = [sampleWithPartialData];
        expectedResult = service.addProgressionToCollectionIfMissing(progressionCollection, progression);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(progression);
      });

      it('should add only unique Progression to an array', () => {
        const progressionArray: IProgression[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const progressionCollection: IProgression[] = [sampleWithRequiredData];
        expectedResult = service.addProgressionToCollectionIfMissing(progressionCollection, ...progressionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const progression: IProgression = sampleWithRequiredData;
        const progression2: IProgression = sampleWithPartialData;
        expectedResult = service.addProgressionToCollectionIfMissing([], progression, progression2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(progression);
        expect(expectedResult).toContain(progression2);
      });

      it('should accept null and undefined values', () => {
        const progression: IProgression = sampleWithRequiredData;
        expectedResult = service.addProgressionToCollectionIfMissing([], null, progression, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(progression);
      });

      it('should return initial array if no Progression is added', () => {
        const progressionCollection: IProgression[] = [sampleWithRequiredData];
        expectedResult = service.addProgressionToCollectionIfMissing(progressionCollection, undefined, null);
        expect(expectedResult).toEqual(progressionCollection);
      });
    });

    describe('compareProgression', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgression(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgression(entity1, entity2);
        const compareResult2 = service.compareProgression(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgression(entity1, entity2);
        const compareResult2 = service.compareProgression(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgression(entity1, entity2);
        const compareResult2 = service.compareProgression(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
