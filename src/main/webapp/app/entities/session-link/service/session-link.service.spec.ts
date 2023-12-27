import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISessionLink } from '../session-link.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../session-link.test-samples';

import { SessionLinkService } from './session-link.service';

const requireRestSample: ISessionLink = {
  ...sampleWithRequiredData,
};

describe('SessionLink Service', () => {
  let service: SessionLinkService;
  let httpMock: HttpTestingController;
  let expectedResult: ISessionLink | ISessionLink[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SessionLinkService);
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

    it('should create a SessionLink', () => {
      const sessionLink = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sessionLink).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SessionLink', () => {
      const sessionLink = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sessionLink).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SessionLink', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SessionLink', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SessionLink', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a SessionLink', () => {
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

    describe('addSessionLinkToCollectionIfMissing', () => {
      it('should add a SessionLink to an empty array', () => {
        const sessionLink: ISessionLink = sampleWithRequiredData;
        expectedResult = service.addSessionLinkToCollectionIfMissing([], sessionLink);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionLink);
      });

      it('should not add a SessionLink to an array that contains it', () => {
        const sessionLink: ISessionLink = sampleWithRequiredData;
        const sessionLinkCollection: ISessionLink[] = [
          {
            ...sessionLink,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSessionLinkToCollectionIfMissing(sessionLinkCollection, sessionLink);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SessionLink to an array that doesn't contain it", () => {
        const sessionLink: ISessionLink = sampleWithRequiredData;
        const sessionLinkCollection: ISessionLink[] = [sampleWithPartialData];
        expectedResult = service.addSessionLinkToCollectionIfMissing(sessionLinkCollection, sessionLink);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionLink);
      });

      it('should add only unique SessionLink to an array', () => {
        const sessionLinkArray: ISessionLink[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sessionLinkCollection: ISessionLink[] = [sampleWithRequiredData];
        expectedResult = service.addSessionLinkToCollectionIfMissing(sessionLinkCollection, ...sessionLinkArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sessionLink: ISessionLink = sampleWithRequiredData;
        const sessionLink2: ISessionLink = sampleWithPartialData;
        expectedResult = service.addSessionLinkToCollectionIfMissing([], sessionLink, sessionLink2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionLink);
        expect(expectedResult).toContain(sessionLink2);
      });

      it('should accept null and undefined values', () => {
        const sessionLink: ISessionLink = sampleWithRequiredData;
        expectedResult = service.addSessionLinkToCollectionIfMissing([], null, sessionLink, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionLink);
      });

      it('should return initial array if no SessionLink is added', () => {
        const sessionLinkCollection: ISessionLink[] = [sampleWithRequiredData];
        expectedResult = service.addSessionLinkToCollectionIfMissing(sessionLinkCollection, undefined, null);
        expect(expectedResult).toEqual(sessionLinkCollection);
      });
    });

    describe('compareSessionLink', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSessionLink(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSessionLink(entity1, entity2);
        const compareResult2 = service.compareSessionLink(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSessionLink(entity1, entity2);
        const compareResult2 = service.compareSessionLink(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSessionLink(entity1, entity2);
        const compareResult2 = service.compareSessionLink(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
