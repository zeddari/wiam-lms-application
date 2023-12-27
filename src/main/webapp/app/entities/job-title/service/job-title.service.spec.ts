import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IJobTitle } from '../job-title.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../job-title.test-samples';

import { JobTitleService } from './job-title.service';

const requireRestSample: IJobTitle = {
  ...sampleWithRequiredData,
};

describe('JobTitle Service', () => {
  let service: JobTitleService;
  let httpMock: HttpTestingController;
  let expectedResult: IJobTitle | IJobTitle[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JobTitleService);
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

    it('should create a JobTitle', () => {
      const jobTitle = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(jobTitle).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a JobTitle', () => {
      const jobTitle = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(jobTitle).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a JobTitle', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of JobTitle', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a JobTitle', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a JobTitle', () => {
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

    describe('addJobTitleToCollectionIfMissing', () => {
      it('should add a JobTitle to an empty array', () => {
        const jobTitle: IJobTitle = sampleWithRequiredData;
        expectedResult = service.addJobTitleToCollectionIfMissing([], jobTitle);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobTitle);
      });

      it('should not add a JobTitle to an array that contains it', () => {
        const jobTitle: IJobTitle = sampleWithRequiredData;
        const jobTitleCollection: IJobTitle[] = [
          {
            ...jobTitle,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJobTitleToCollectionIfMissing(jobTitleCollection, jobTitle);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a JobTitle to an array that doesn't contain it", () => {
        const jobTitle: IJobTitle = sampleWithRequiredData;
        const jobTitleCollection: IJobTitle[] = [sampleWithPartialData];
        expectedResult = service.addJobTitleToCollectionIfMissing(jobTitleCollection, jobTitle);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobTitle);
      });

      it('should add only unique JobTitle to an array', () => {
        const jobTitleArray: IJobTitle[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const jobTitleCollection: IJobTitle[] = [sampleWithRequiredData];
        expectedResult = service.addJobTitleToCollectionIfMissing(jobTitleCollection, ...jobTitleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const jobTitle: IJobTitle = sampleWithRequiredData;
        const jobTitle2: IJobTitle = sampleWithPartialData;
        expectedResult = service.addJobTitleToCollectionIfMissing([], jobTitle, jobTitle2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobTitle);
        expect(expectedResult).toContain(jobTitle2);
      });

      it('should accept null and undefined values', () => {
        const jobTitle: IJobTitle = sampleWithRequiredData;
        expectedResult = service.addJobTitleToCollectionIfMissing([], null, jobTitle, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobTitle);
      });

      it('should return initial array if no JobTitle is added', () => {
        const jobTitleCollection: IJobTitle[] = [sampleWithRequiredData];
        expectedResult = service.addJobTitleToCollectionIfMissing(jobTitleCollection, undefined, null);
        expect(expectedResult).toEqual(jobTitleCollection);
      });
    });

    describe('compareJobTitle', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJobTitle(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareJobTitle(entity1, entity2);
        const compareResult2 = service.compareJobTitle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareJobTitle(entity1, entity2);
        const compareResult2 = service.compareJobTitle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareJobTitle(entity1, entity2);
        const compareResult2 = service.compareJobTitle(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
