import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ISponsor } from '../sponsor.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../sponsor.test-samples';

import { SponsorService, RestSponsor } from './sponsor.service';

const requireRestSample: RestSponsor = {
  ...sampleWithRequiredData,
  birthdate: sampleWithRequiredData.birthdate?.format(DATE_FORMAT),
};

describe('Sponsor Service', () => {
  let service: SponsorService;
  let httpMock: HttpTestingController;
  let expectedResult: ISponsor | ISponsor[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SponsorService);
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

    it('should create a Sponsor', () => {
      const sponsor = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sponsor).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sponsor', () => {
      const sponsor = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sponsor).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sponsor', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sponsor', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Sponsor', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Sponsor', () => {
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

    describe('addSponsorToCollectionIfMissing', () => {
      it('should add a Sponsor to an empty array', () => {
        const sponsor: ISponsor = sampleWithRequiredData;
        expectedResult = service.addSponsorToCollectionIfMissing([], sponsor);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sponsor);
      });

      it('should not add a Sponsor to an array that contains it', () => {
        const sponsor: ISponsor = sampleWithRequiredData;
        const sponsorCollection: ISponsor[] = [
          {
            ...sponsor,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSponsorToCollectionIfMissing(sponsorCollection, sponsor);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sponsor to an array that doesn't contain it", () => {
        const sponsor: ISponsor = sampleWithRequiredData;
        const sponsorCollection: ISponsor[] = [sampleWithPartialData];
        expectedResult = service.addSponsorToCollectionIfMissing(sponsorCollection, sponsor);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sponsor);
      });

      it('should add only unique Sponsor to an array', () => {
        const sponsorArray: ISponsor[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sponsorCollection: ISponsor[] = [sampleWithRequiredData];
        expectedResult = service.addSponsorToCollectionIfMissing(sponsorCollection, ...sponsorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sponsor: ISponsor = sampleWithRequiredData;
        const sponsor2: ISponsor = sampleWithPartialData;
        expectedResult = service.addSponsorToCollectionIfMissing([], sponsor, sponsor2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sponsor);
        expect(expectedResult).toContain(sponsor2);
      });

      it('should accept null and undefined values', () => {
        const sponsor: ISponsor = sampleWithRequiredData;
        expectedResult = service.addSponsorToCollectionIfMissing([], null, sponsor, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sponsor);
      });

      it('should return initial array if no Sponsor is added', () => {
        const sponsorCollection: ISponsor[] = [sampleWithRequiredData];
        expectedResult = service.addSponsorToCollectionIfMissing(sponsorCollection, undefined, null);
        expect(expectedResult).toEqual(sponsorCollection);
      });
    });

    describe('compareSponsor', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSponsor(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSponsor(entity1, entity2);
        const compareResult2 = service.compareSponsor(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSponsor(entity1, entity2);
        const compareResult2 = service.compareSponsor(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSponsor(entity1, entity2);
        const compareResult2 = service.compareSponsor(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
