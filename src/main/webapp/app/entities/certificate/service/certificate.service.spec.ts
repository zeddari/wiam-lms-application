import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICertificate } from '../certificate.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../certificate.test-samples';

import { CertificateService } from './certificate.service';

const requireRestSample: ICertificate = {
  ...sampleWithRequiredData,
};

describe('Certificate Service', () => {
  let service: CertificateService;
  let httpMock: HttpTestingController;
  let expectedResult: ICertificate | ICertificate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CertificateService);
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

    it('should create a Certificate', () => {
      const certificate = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(certificate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Certificate', () => {
      const certificate = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(certificate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Certificate', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Certificate', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Certificate', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Certificate', () => {
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

    describe('addCertificateToCollectionIfMissing', () => {
      it('should add a Certificate to an empty array', () => {
        const certificate: ICertificate = sampleWithRequiredData;
        expectedResult = service.addCertificateToCollectionIfMissing([], certificate);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(certificate);
      });

      it('should not add a Certificate to an array that contains it', () => {
        const certificate: ICertificate = sampleWithRequiredData;
        const certificateCollection: ICertificate[] = [
          {
            ...certificate,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, certificate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Certificate to an array that doesn't contain it", () => {
        const certificate: ICertificate = sampleWithRequiredData;
        const certificateCollection: ICertificate[] = [sampleWithPartialData];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, certificate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(certificate);
      });

      it('should add only unique Certificate to an array', () => {
        const certificateArray: ICertificate[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const certificateCollection: ICertificate[] = [sampleWithRequiredData];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, ...certificateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const certificate: ICertificate = sampleWithRequiredData;
        const certificate2: ICertificate = sampleWithPartialData;
        expectedResult = service.addCertificateToCollectionIfMissing([], certificate, certificate2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(certificate);
        expect(expectedResult).toContain(certificate2);
      });

      it('should accept null and undefined values', () => {
        const certificate: ICertificate = sampleWithRequiredData;
        expectedResult = service.addCertificateToCollectionIfMissing([], null, certificate, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(certificate);
      });

      it('should return initial array if no Certificate is added', () => {
        const certificateCollection: ICertificate[] = [sampleWithRequiredData];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, undefined, null);
        expect(expectedResult).toEqual(certificateCollection);
      });
    });

    describe('compareCertificate', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCertificate(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCertificate(entity1, entity2);
        const compareResult2 = service.compareCertificate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCertificate(entity1, entity2);
        const compareResult2 = service.compareCertificate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCertificate(entity1, entity2);
        const compareResult2 = service.compareCertificate(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
