import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgressionMode } from '../progression-mode.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../progression-mode.test-samples';

import { ProgressionModeService } from './progression-mode.service';

const requireRestSample: IProgressionMode = {
  ...sampleWithRequiredData,
};

describe('ProgressionMode Service', () => {
  let service: ProgressionModeService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgressionMode | IProgressionMode[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgressionModeService);
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

    it('should create a ProgressionMode', () => {
      const progressionMode = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(progressionMode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgressionMode', () => {
      const progressionMode = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(progressionMode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgressionMode', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgressionMode', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgressionMode', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a ProgressionMode', () => {
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

    describe('addProgressionModeToCollectionIfMissing', () => {
      it('should add a ProgressionMode to an empty array', () => {
        const progressionMode: IProgressionMode = sampleWithRequiredData;
        expectedResult = service.addProgressionModeToCollectionIfMissing([], progressionMode);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(progressionMode);
      });

      it('should not add a ProgressionMode to an array that contains it', () => {
        const progressionMode: IProgressionMode = sampleWithRequiredData;
        const progressionModeCollection: IProgressionMode[] = [
          {
            ...progressionMode,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgressionModeToCollectionIfMissing(progressionModeCollection, progressionMode);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgressionMode to an array that doesn't contain it", () => {
        const progressionMode: IProgressionMode = sampleWithRequiredData;
        const progressionModeCollection: IProgressionMode[] = [sampleWithPartialData];
        expectedResult = service.addProgressionModeToCollectionIfMissing(progressionModeCollection, progressionMode);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(progressionMode);
      });

      it('should add only unique ProgressionMode to an array', () => {
        const progressionModeArray: IProgressionMode[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const progressionModeCollection: IProgressionMode[] = [sampleWithRequiredData];
        expectedResult = service.addProgressionModeToCollectionIfMissing(progressionModeCollection, ...progressionModeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const progressionMode: IProgressionMode = sampleWithRequiredData;
        const progressionMode2: IProgressionMode = sampleWithPartialData;
        expectedResult = service.addProgressionModeToCollectionIfMissing([], progressionMode, progressionMode2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(progressionMode);
        expect(expectedResult).toContain(progressionMode2);
      });

      it('should accept null and undefined values', () => {
        const progressionMode: IProgressionMode = sampleWithRequiredData;
        expectedResult = service.addProgressionModeToCollectionIfMissing([], null, progressionMode, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(progressionMode);
      });

      it('should return initial array if no ProgressionMode is added', () => {
        const progressionModeCollection: IProgressionMode[] = [sampleWithRequiredData];
        expectedResult = service.addProgressionModeToCollectionIfMissing(progressionModeCollection, undefined, null);
        expect(expectedResult).toEqual(progressionModeCollection);
      });
    });

    describe('compareProgressionMode', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgressionMode(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgressionMode(entity1, entity2);
        const compareResult2 = service.compareProgressionMode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgressionMode(entity1, entity2);
        const compareResult2 = service.compareProgressionMode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgressionMode(entity1, entity2);
        const compareResult2 = service.compareProgressionMode(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
