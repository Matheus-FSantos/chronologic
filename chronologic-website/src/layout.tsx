import { AppSidebar } from "./components/internal/sidebar";
import { SidebarProvider } from "./components/ui/sidebar";

type LayoutProps = {
  children: React.ReactNode
}

const Layout = ({ children }: LayoutProps): React.ReactElement => {
  return (
    <SidebarProvider>
      <AppSidebar activedItem="dashboard-page"/>
      <main>
        { children }
      </main>
    </SidebarProvider>
  );
}

export { Layout };