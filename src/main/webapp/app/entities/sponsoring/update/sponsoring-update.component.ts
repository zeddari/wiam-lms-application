import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISponsor } from 'app/entities/sponsor/sponsor.model';
import { SponsorService } from 'app/entities/sponsor/service/sponsor.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { ICurrency } from 'app/entities/currency/currency.model';
import { CurrencyService } from 'app/entities/currency/service/currency.service';
import { SponsoringService } from '../service/sponsoring.service';
import { ISponsoring } from '../sponsoring.model';
import { SponsoringFormService, SponsoringFormGroup } from './sponsoring-form.service';

@Component({
  standalone: true,
  selector: 'jhi-sponsoring-update',
  templateUrl: './sponsoring-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SponsoringUpdateComponent implements OnInit {
  isSaving = false;
  sponsoring: ISponsoring | null = null;

  sponsorsSharedCollection: ISponsor[] = [];
  projectsSharedCollection: IProject[] = [];
  currenciesSharedCollection: ICurrency[] = [];

  editForm: SponsoringFormGroup = this.sponsoringFormService.createSponsoringFormGroup();

  constructor(
    protected sponsoringService: SponsoringService,
    protected sponsoringFormService: SponsoringFormService,
    protected sponsorService: SponsorService,
    protected projectService: ProjectService,
    protected currencyService: CurrencyService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareSponsor = (o1: ISponsor | null, o2: ISponsor | null): boolean => this.sponsorService.compareSponsor(o1, o2);

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  compareCurrency = (o1: ICurrency | null, o2: ICurrency | null): boolean => this.currencyService.compareCurrency(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sponsoring }) => {
      this.sponsoring = sponsoring;
      if (sponsoring) {
        this.updateForm(sponsoring);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sponsoring = this.sponsoringFormService.getSponsoring(this.editForm);
    if (sponsoring.id !== null) {
      this.subscribeToSaveResponse(this.sponsoringService.update(sponsoring));
    } else {
      this.subscribeToSaveResponse(this.sponsoringService.create(sponsoring));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISponsoring>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(sponsoring: ISponsoring): void {
    this.sponsoring = sponsoring;
    this.sponsoringFormService.resetForm(this.editForm, sponsoring);

    this.sponsorsSharedCollection = this.sponsorService.addSponsorToCollectionIfMissing<ISponsor>(
      this.sponsorsSharedCollection,
      sponsoring.sponsor,
    );
    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      sponsoring.project,
    );
    this.currenciesSharedCollection = this.currencyService.addCurrencyToCollectionIfMissing<ICurrency>(
      this.currenciesSharedCollection,
      sponsoring.currency,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.sponsorService
      .query()
      .pipe(map((res: HttpResponse<ISponsor[]>) => res.body ?? []))
      .pipe(
        map((sponsors: ISponsor[]) => this.sponsorService.addSponsorToCollectionIfMissing<ISponsor>(sponsors, this.sponsoring?.sponsor)),
      )
      .subscribe((sponsors: ISponsor[]) => (this.sponsorsSharedCollection = sponsors));

    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.sponsoring?.project)),
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));

    this.currencyService
      .query()
      .pipe(map((res: HttpResponse<ICurrency[]>) => res.body ?? []))
      .pipe(
        map((currencies: ICurrency[]) =>
          this.currencyService.addCurrencyToCollectionIfMissing<ICurrency>(currencies, this.sponsoring?.currency),
        ),
      )
      .subscribe((currencies: ICurrency[]) => (this.currenciesSharedCollection = currencies));
  }
}
